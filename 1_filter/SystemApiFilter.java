package kr.com.dreamsecurity.hotp.filter;

import kr.com.dreamsecurity.hotp.constants.RESULT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 
 * system api filter 
 * 
 * @author Park_Kwang_Ho
 *
 */
public class SystemApiFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {


            //// HTTP request flow ////
            logger.info("[System RESTful API] request");

            // request 인증
            RequestBodyWrapper requestWrapper = new RequestBodyWrapper(httpRequest);

            if(requestWrapper.getResult() == RESULT.SUCCESS){        // filter pass

                logger.info("<request result> Success auth in http request filter ");

                //  다음 필터 또는 servlet 으로 request 를 전달함 (기점)
                chain.doFilter(requestWrapper, httpResponse);

            }else if(requestWrapper.getResult() == RESULT.FAIL){      // filter deny

                logger.info("<request result> Fail auth in http request filter ");
                String ResponseMessage = "응답 메세지";
                byte[] data = ResponseMessage.getBytes("utf-8");
                int count = data.length;

                httpResponse.setStatus(500);
                httpResponse.setContentLength(count);
                httpResponse.getOutputStream().write(data);
                httpResponse.flushBuffer();
            }


            //// HTTP response flow ////
            logger.info("[System RESTful API] response");

        } catch (Exception e) {

            e.printStackTrace();
            String ResponseMessage = "응답 메세지";
            byte[] data = ResponseMessage.getBytes("utf-8");
            int count = data.length;

            httpResponse.setStatus(400);
            httpResponse.setContentLength(count);
            httpResponse.getOutputStream().write(data);
            httpResponse.flushBuffer();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("filter instanse init");
    }

    @Override
    public void destroy() {
        System.out.println("finish filter instanse");
    }

}