package com.zara.Zara.checks;

import com.zara.Zara.services.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckingUtils {

    @Autowired
   public static IUserService userService;
   public static Logger LOGGER = LogManager.getLogger(CheckingUtils.class);


}
