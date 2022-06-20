package hw;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;

import static hw.SessionProvider.*;

@WebServlet("/evening")
public class EveningServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        Cookie[] cookies = req.getCookies();
        Session session = getOrCreateSession(cookies, name, 100, resp);

        if (Objects.nonNull(name)) {
            print(name, resp.getWriter());
            resp.sendRedirect("/evening");
        } else if (Objects.nonNull(session)) {
            print(session.getValue(), resp.getWriter());
        } else {
            print("Buddy", resp.getWriter());
        }
    }

    private void print(String name, PrintWriter writer) {
        writer.printf("Good evening, %s!%n", name);
    }
}
