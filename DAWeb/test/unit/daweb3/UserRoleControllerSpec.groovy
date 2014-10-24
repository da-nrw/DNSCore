package daweb3



import grails.test.mixin.*
import spock.lang.*

@TestFor(UserRoleController)
@Mock(UserRole)
class UserRoleControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.userRoleInstanceList
            model.userRoleInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.userRoleInstance!= null
    }


 


   
}
