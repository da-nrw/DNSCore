package daweb3



import grails.test.mixin.*
import spock.lang.*

@TestFor(PreservationSystemController)
@Mock(PreservationSystem)
class PreservationSystemControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.preservationSystemInstanceList
            model.preservationSystemInstanceCount == 0
    }

  

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def preservationSystem = new PreservationSystem(params)
            controller.show(preservationSystem)

        then:"A model is populated containing the domain instance"
            model.preservationSystemInstance == preservationSystem
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def preservationSystem = new PreservationSystem(params)
            controller.edit(preservationSystem)

        then:"A model is populated containing the domain instance"
            model.preservationSystemInstance == preservationSystem
    }


 
}
