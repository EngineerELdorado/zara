package com.zara.Zara.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import static com.zara.Zara.constants.Keys.RESPONSE_CODE;
import static com.zara.Zara.constants.Keys.RESPONSE_MESSAGE;

@Component
public class CORSFilter implements Filter {

    private final Logger LOG = LogManager.getLogger(CORSFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Expose-Headers", RESPONSE_CODE+","+RESPONSE_MESSAGE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "72000");
        chain.doFilter(req, res);
        //LOG.info(".......the filter has been chained........");
    }

    @Override
    public void init(FilterConfig filterConfig)
    {
      //  LOG.info(".......CORS Filter started...");
    }

    @Override
    public void destroy() {

        //LOG.info("...CORS Filter destroyed...");
    }

}
