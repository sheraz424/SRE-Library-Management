package LMS;

import java.io.IOException;

public interface Handler {
    void handle(Person person, int choice, Library lib) throws IOException;
}