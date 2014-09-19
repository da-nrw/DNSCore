package de.uzk.hki.da.cb;

import java.lang.reflect.Field;

public class ActionUnderTestAnnotationParser {

	   public Field parse(Class<?> clazz) throws Exception {
	      Field[] fields = clazz.getDeclaredFields();
	      for (Field method : fields) {
	         if (method.isAnnotationPresent(ActionUnderTest.class)) {
	        	 return method;
	         }
	      }
	      return null;
	   }
	}
