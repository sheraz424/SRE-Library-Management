package LMS;

import java.io.*;
import java.util.*;
import java.sql.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // Clear screen
    public static void clrscr() {
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }
    }

    // Safe input
    public static int takeInput(int min, int max) {
        while (true) {
            System.out.print("\nEnter Choice: ");
            String choice = scanner.next();

            try {
                int value = Integer.parseInt(choice);

                if (value > min && value < max) {
                    return value;
                }

            } catch (Exception e) {
            }

            System.out.println("Invalid Input.");
        }
    }

    // ---------------- COMMON FUNCTIONS ----------------

   public static void allFunctionalities(Person person, int choice) throws IOException {

    Library lib = Library.getInstance();

    Handler handler;

    if (person instanceof Borrower) {
        handler = new BorrowerHandler();
    } 
    else if (person instanceof Clerk) {
        handler = new ClerkHandler();
    } 
    else {
        handler = new LibrarianHandler();
    }

    handler.handle(person, choice, lib);
}

    // ---------------- HELPER METHODS ----------------

    public static void handleHoldRequest(Person person, Library lib) {

        ArrayList<Book> books = lib.searchForBooks();

        if (books != null) {

            int input = takeInput(-1, books.size());
            Book book = books.get(input);

            if (person instanceof Clerk || person instanceof Librarian) {

                Borrower borrower = lib.findBorrower();

                if (borrower != null) {
                    book.makeHoldRequest(borrower);
                }

            } else {
                book.makeHoldRequest((Borrower) person);
            }
        }
    }

    public static void handleViewInfo(Person person, Library lib) {

        if (person instanceof Clerk || person instanceof Librarian) {

            Borrower borrower = lib.findBorrower();

            if (borrower != null) {
                borrower.printInfo();
            }

        } else {
            person.printInfo();
        }
    }

    public static void handleFine(Person person, Library lib) {

        double fine = 0;

        if (person instanceof Clerk || person instanceof Librarian) {

            Borrower borrower = lib.findBorrower();

            if (borrower != null) {
                fine = lib.computeFine2(borrower);
            }

        } else {
            fine = lib.computeFine2((Borrower) person);
        }

        System.out.println("\nTotal Fine: Rs " + fine);
    }

    public static void handleHoldQueue(Library lib) {

        ArrayList<Book> books = lib.searchForBooks();

        if (books != null) {
            int input = takeInput(-1, books.size());
            books.get(input).printHoldRequests();
        }
    }

    public static void handleIssueBook(Person person, Library lib) {

        ArrayList<Book> books = lib.searchForBooks();

        if (books != null) {

            int input = takeInput(-1, books.size());
            Book book = books.get(input);

            Borrower borrower = lib.findBorrower();

            if (borrower != null) {
                book.issueBook(borrower, (Staff) person);
            }
        }
    }

    public static void handleReturnBook(Person person, Library lib) {

        Borrower borrower = lib.findBorrower();

        if (borrower != null) {

            borrower.printBorrowedBooks();

            ArrayList<Loan> loans = borrower.getBorrowedBooks();

            if (!loans.isEmpty()) {

                int input = takeInput(-1, loans.size());

                Loan loan = loans.get(input);

                loan.getBook().returnBook(borrower, loan, (Staff) person);

            } else {
                System.out.println("No borrowed books found.");
            }
        }
    }

    public static void handleRenewBook(Library lib) {

        Borrower borrower = lib.findBorrower();

        if (borrower != null) {

            borrower.printBorrowedBooks();

            ArrayList<Loan> loans = borrower.getBorrowedBooks();

            if (!loans.isEmpty()) {

                int input = takeInput(-1, loans.size());
                loans.get(input).renewIssuedBook(new java.util.Date());

            } else {
                System.out.println("No books available for renewal.");
            }
        }
    }

    public static void updateBorrower(Library lib) {

        Borrower borrower = lib.findBorrower();

        if (borrower != null) {
            borrower.updateBorrowerInfo();
        }
    }

    public static void addBook(Library lib) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter Title: ");
        String title = reader.readLine();

        System.out.print("Enter Subject: ");
        String subject = reader.readLine();

        System.out.print("Enter Author: ");
        String author = reader.readLine();

        lib.createBook(title, subject, author);
    }

    public static void removeBook(Library lib) {

        ArrayList<Book> books = lib.searchForBooks();

        if (books != null) {
            int input = takeInput(-1, books.size());
            lib.removeBookfromLibrary(books.get(input));
        }
    }

    public static void updateBook(Library lib) {

        ArrayList<Book> books = lib.searchForBooks();

        if (books != null) {
            int input = takeInput(-1, books.size());
            books.get(input).changeBookInfo();
        }
    }

    public static void viewClerk(Library lib) {

        Clerk clerk = lib.findClerk();

        if (clerk != null) {
            clerk.printInfo();
        }
    }

    // ---------------- MAIN ----------------

    public static void main(String[] args) {

        Library lib = Library.getInstance();

        lib.setFine(20);
        lib.setRequestExpiry(7);
        lib.setReturnDeadline(5);
        lib.setName("FAST Library");

        Connection con = lib.makeConnection();

        if (con == null) {
            System.out.println("Database Connection Failed.");
            return;
        }

        try {

            lib.populateLibrary(con);

            boolean stop = false;

            while (!stop) {

                clrscr();

                System.out.println("====== LIBRARY MANAGEMENT SYSTEM ======");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.println("3. Admin");

                int choice = takeInput(0, 4);

                if (choice == 1) {

                    Person person = lib.login();

                    if (person != null) {

                        while (true) {

                            System.out.println("\n1 Search");
                            System.out.println("2 Hold");
                            System.out.println("3 View Info");
                            System.out.println("4 Fine");
                            System.out.println("5 Queue");
                            System.out.println("6 Logout");

                            int userChoice = takeInput(0, 7);

                            if (userChoice == 6)
                                break;

                            allFunctionalities(person, userChoice);
                        }
                    }

                } else if (choice == 3) {

                    System.out.print("Enter Password: ");
                    String pass = scanner.next();

                    if (pass.equals("lib")) {
                        lib.viewAllBooks();
                    }

                } else {
                    stop = true;
                }
            }

            lib.fillItBack(con);

        } catch (Exception e) {
            System.out.println("Program Closed.");
        }
    }
}