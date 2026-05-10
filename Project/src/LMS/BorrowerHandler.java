package LMS;

import java.io.IOException;

public class BorrowerHandler implements Handler {

    @Override
    public void handle(Person person, int choice, Library lib) throws IOException {

        Borrower borrower = (Borrower) person;

        switch (choice) {
            case 1 -> lib.searchForBooks();

            case 2 -> {
                var books = lib.searchForBooks();
                if (books != null) {
                    Book b = books.get(0);
                    b.makeHoldRequest(borrower);
                }
            }

            case 3 -> borrower.printInfo();

            case 4 -> {
                double fine = lib.computeFine2(borrower);
                System.out.println("Total Fine: " + fine);
            }

            case 5 -> lib.searchForBooks();

            default -> System.out.println("Invalid option");
        }
    }
}