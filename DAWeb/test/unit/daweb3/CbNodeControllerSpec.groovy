package daweb3



import grails.test.mixin.*
import spock.lang.*

@TestFor(CbNodeController)
@Mock(CbNode)
class CbNodeControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.cbNodeInstanceList
            model.cbNodeInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.cbNodeInstance!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            def cbNode = new CbNode()
            cbNode.validate()
            controller.save(cbNode)

        then:"The create view is rendered again with the correct model"
            model.cbNodeInstance!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            cbNode = new CbNode(params)

            controller.save(cbNode)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/cbNode/show/1'
            controller.flash.message != null
            CbNode.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def cbNode = new CbNode(params)
            controller.show(cbNode)

        then:"A model is populated containing the domain instance"
            model.cbNodeInstance == cbNode
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def cbNode = new CbNode(params)
            controller.edit(cbNode)

        then:"A model is populated containing the domain instance"
            model.cbNodeInstance == cbNode
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/cbNode/index'
            flash.message != null


        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def cbNode = new CbNode()
            cbNode.validate()
            controller.update(cbNode)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.cbNodeInstance == cbNode

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            cbNode = new CbNode(params).save(flush: true)
            controller.update(cbNode)

        then:"A redirect is issues to the show action"
            response.redirectedUrl == "/cbNode/show/$cbNode.id"
            flash.message != null
    }

}
