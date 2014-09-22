package de.uzk.hki.da.cb;

import java.lang.reflect.Field;

public class ActionUnderTestAnnotationParser {

	   public Field parse(Class<?> clazz) throws Exception {
	      Field[] fields = clazz.getDeclaredFields();
	      for (Field field : fields) {
	         if (field.isAnnotationPresent(ActionUnderTest.class)) {
	        	 return field;
	         }
	      }
	      return null;
	   }
	}
