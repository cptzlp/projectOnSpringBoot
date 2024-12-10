package ru.max.springcourse.projectonspringboot.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import ru.max.springcourse.projectonspringboot.dto.BookDto;
import ru.max.springcourse.projectonspringboot.service.BookService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Controller
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private final BookService bookService = BookService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write("<h1> Список книг </h1>");
            writer.write("<ul>");
            bookService.findAll().forEach(bookDto ->
                    writer.write("""
                                 <li>
                                 <a href='/persons?bookId=%d'>%s</a>
                                 </li>
                            """.formatted(bookDto.id(), bookDto.description())));
            writer.write("</ul>");
        }
    }
}
