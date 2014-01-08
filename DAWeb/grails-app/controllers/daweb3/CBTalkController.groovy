package daweb3

import de.uzk.hki.da.core.ActionDescription
import de.uzk.hki.da.utils.MySocketRequest

class CBTalkController {
	
    def index() { 	
		MySocketRequest mySocket = new MySocketRequest("localhost", 4455);
		def myList = mySocket.getActions();
		[myList:myList]
	   }	
	
	
		
		
}
