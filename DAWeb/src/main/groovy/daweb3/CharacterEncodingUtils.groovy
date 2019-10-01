/**
 * 
 */
package daweb3

import javax.servlet.http.HttpServletResponse;

/**
 * @author gbender
 *
 */
class CharacterEncodingUtils {

	 void setEncoding(HttpServletResponse response) {
		response.characterEncoding = 'UTF-8'
	 }
}
