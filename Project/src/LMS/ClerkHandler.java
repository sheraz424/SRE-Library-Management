package LMS;

import java.io.IOException;
import java.util.ArrayList;

public class ClerkHandler implements Handler {

    @Override
    public void handle(Person person, int choice, Library lib) throws IOException {

        Clerk clerk = (Clerk) person;

        switch (choice) {

            case 1 -> lib.searchForBooks();

            case 6 -> {
                ArrayList<Book> books = lib.searchForBooks();
                if (books != null) {
                    Book b = books.get(0);
                    Borrower bor = lib.findBorrower();
                    b.issueBook(bor, clerk);
                }
            }

            case 7 -> {
                Borrower bor = lib.findBorrower();
                if (bor != null) {
                    var loans = bor.getBorrowedBooks();
                    if (!loans.isEmpty()) {
                        loans.get(0).getBook().returnBook(bor, loans.get(0), clerk);
                    }
                }
            }

            default -> System.out.println("Option handled in clerk module");
        }
    }
}