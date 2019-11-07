import com.beisert.demo.spring.boot.rest.product.ProductApplication
import com.beisert.demo.spring.boot.rest.product.entities.Product
import com.beisert.demo.spring.boot.rest.product.repo.ProductRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import spock.lang.Specification

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ProductApplication)
class ProductControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    ProductRepository repository;

    def cleanup() {
        this.repository.deleteAll()
    }

    def "POST /products/"() {
        when:
        def product = new Product(name: "David", price: new BigDecimal(10.1))
        def entity = restTemplate.postForEntity('/api/products/', product, Map)
        print "Posted product. Result=" + entity.body

        then:
        entity.statusCode == HttpStatus.CREATED
        entity.body.name == 'David'

    }

    def "GET /products/"() {
        setup:
        def product = new Product(name: "David", price: new BigDecimal(10.1))
        this.repository.save(product)

        when:
        def entity = restTemplate.getForEntity('/api/products/', Map)

        then:
        entity.statusCode == HttpStatus.OK
        print entity.body.content
        entity.body.content.size == 1
    }

    def "PATCH /products/1"() {
        setup:
        def product = new Product(name: "David", price: new BigDecimal(10.1))
        this.repository.save(product)

        when:
        product = new Product(sku:  product.sku, name: "David2", price: new BigDecimal(10.1))
        def reqEntity = new HttpEntity(product);
        def entity = restTemplate.exchange('/api/products/' + product.sku, HttpMethod.PUT, reqEntity, Map)

        then:
        entity.statusCode == HttpStatus.OK
        print entity.body
        entity.body.name == "David2"
    }

    def "DELETE /products/1"() {
        setup:
        def product = new Product(name: "David", price: new BigDecimal(10.1))
        this.repository.save(product)

        when:
        restTemplate.delete('/api/products/' + product.sku)

        then:
        def entity = restTemplate.getForEntity('/api/products/', Map)
        print entity.body
        entity.body.content.size == 0
    }


}
