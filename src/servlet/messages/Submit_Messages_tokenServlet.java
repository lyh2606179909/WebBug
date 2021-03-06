package servlet.messages;

import dao.MessagesDao;
import entity.User;
import util.Tool;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "Submit_Messages_tokenServlet")
public class Submit_Messages_tokenServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();

        // 创建会话Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user"); //获得session 会话中的 user

        if (user == null) {
            writer.write(
                    "<h2>Writer Messages</h2>"
                            + "抱歉，该页面需要登陆之后才能进行操作！ 2秒后跳转登录页！"
            );
            //等待2秒 跳转登录页
            response.setHeader("refresh", "2;url=/jsp/join/login.jsp");
        } else {

            // examine csrf token
            String csrf_token = request.getParameter("csrf_token");
            String csrf_token_s = (String) session.getAttribute("csrf_token"); //获得session 会话中的 user

            System.out.println(csrf_token + "---------" + csrf_token_s);
            if (!(csrf_token.equals(csrf_token_s))) {
                //后台强制更新 token
                Tool tool = new Tool();
                session.setAttribute("csrf_token", tool.getRandomReqNo());

                writer.write(
                        "<h2>Writer Messages</h2>"
                                + "抱歉，留言失败！"
                );
                //等待2秒 跳转
//                response.setHeader("refresh", "2;url=/WEB-INF/jsp/messages/writermessages_token.jsp");
            } else {

                //后台强制更新 token
                Tool tool = new Tool();
                session.setAttribute("csrf_token", tool.getRandomReqNo());

                MessagesDao messagesDao = new MessagesDao();
                String title = request.getParameter("title");
                String message = request.getParameter("message");

                //System.out.println("打印username:" + user.getUsername());

                boolean b = messagesDao.addInfo(user.getUsername(), title, message);
                if (b) {
                    response.sendRedirect("/ShowMessagesServlet");
                } else {
                    writer.write(
                            "<h2>Writer Messages</h2>"
                                    + "抱歉，留言失败 ！"
                    );
                    //等待2秒 跳转登录页
//                    response.setHeader("refresh", "2;url=/WEB-INF/jsp/messages/writermessage_token.jsp");
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
