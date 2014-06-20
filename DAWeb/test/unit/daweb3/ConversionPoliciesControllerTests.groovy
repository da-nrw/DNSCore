package daweb3



import org.junit.*
import grails.test.mixin.*

@TestFor(ConversionPoliciesController)
@Mock(ConversionPolicies)
class ConversionPoliciesControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }
	
	

    void testIndex() {
		def request = GrailsWebUtil.bindMockWebRequest()
		def contractor = new Contractor();
		request.session['contractor'] = contractor
        controller.index()
        assert "/conversionPolicies/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.conversionPoliciesInstanceList.size() == 0
        assert model.conversionPoliciesInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.conversionPoliciesInstance != null
    }


    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/conversionPolicies/list'

        populateValidParams(params)
        def conversionPolicies = new ConversionPolicies(params)

        assert conversionPolicies.save() != null

        params.id = conversionPolicies.id

        def model = controller.show()

        assert model.conversionPoliciesInstance == conversionPolicies
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/conversionPolicies/list'

        populateValidParams(params)
        def conversionPolicies = new ConversionPolicies(params)

        assert conversionPolicies.save() != null

        params.id = conversionPolicies.id

        def model = controller.edit()

        assert model.conversionPoliciesInstance == conversionPolicies
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/conversionPolicies/list'

        response.reset()

        populateValidParams(params)
        def conversionPolicies = new ConversionPolicies(params)

        assert conversionPolicies.save() != null

        // test invalid parameters in update
        params.id = conversionPolicies.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/conversionPolicies/edit"
        assert model.conversionPoliciesInstance != null

        conversionPolicies.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/conversionPolicies/show/$conversionPolicies.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        conversionPolicies.clearErrors()

        populateValidParams(params)
        params.id = conversionPolicies.id
        params.version = -1
        controller.update()

        assert view == "/conversionPolicies/edit"
        assert model.conversionPoliciesInstance != null
        assert model.conversionPoliciesInstance.errors.getFieldError('version')
        assert flash.message != null
    }

}
