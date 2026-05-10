package LMS;

import java.io.IOException;

public class LibrarianHandler implements Handler {

    @Override
    public void handle(Person person, int choice, Library lib) throws IOException {

        Librarian librarian = (Librarian) person;

        switch (choice) {

            case 11 -> {
                lib.createBook("Title", "Subject", "Author");
            }

            case 12 -> {
                var books = lib.searchForBooks();
                if (books != null) {
                    lib.removeBookfromLibrary(books.get(0));
                }
            }

            case 14 -> {
                Clerk clerk = lib.findClerk();
                if (clerk != null) clerk.printInfo();
            }

            default -> System.out.println("Handled in librarian module");
        }
    }
}