import com.beisert.demo.spring.boot.rest.product.ProductApplication
import com.beisert.demo.spring.boot.rest.product.entities.Order
import com.beisert.demo.spring.boot.rest.product.entities.Product
import com.beisert.demo.spring.boot.rest.product.repo.OrderRepository
import com.beisert.demo.spring.boot.rest.product.repo.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.text.SimpleDateFormat

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ProductApplication)
class OrderControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @LocalServerPort
    int randomServerPort;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    def setup() {
        // create 10 products
        for (int i = 0; i < 10; i++) {
            def product = new Product(name: "David" + i, price: new BigDecimal("1.0" + i))
            this.productRepository.save(product)
        }
    }

    def cleanup() {
        this.productRepository.deleteAll()
        this.orderRepository.deleteAll()
    }

    def "POST /orders/"() {
        when:
        def productList = new ArrayList<Product>(this.productRepository.findAll())
        def ordered = dateFormat.parse("2019-10-01")
        def order = new Order(buyersEmail: "beisdog@web.de", products: productList, orderedAt: ordered);

        def entityOrder = restTemplate.postForEntity('/api/orders/', order, Map)
        print "posted order. Result " + entityOrder.body

        then:
        // test order
        entityOrder.statusCode == HttpStatus.CREATED
        entityOrder.body.buyersEmail == 'beisdog@web.de'
        // test products
        entityOrder.body.products.size == 10
    }

    def "GET /orders/"() {
        setup:
        def products = new ArrayList<Product>(this.productRepository.findAll())
        def order = new Order(buyersEmail: "beisdog@web.de", products: products)
        this.orderRepository.save(order)

        when:
        def entity = restTemplate.getForEntity('/api/orders/', Map)

        then:
        entity.statusCode == HttpStatus.OK
        print entity.body.content
        entity.body.content.size == 1
    }

    def "PUT /orders/1"() {
        setup:
        def productList = new ArrayList<Product>(this.productRepository.findAll())
        Order order = new Order(buyersEmail: "beisdog@web.de", products: productList)
        order = this.orderRepository.save(order)

        when:
        def orderMap = [buyersEmail: "beisdog2@web.de"]
        def reqEntity = new HttpEntity(orderMap);
        def entityOrder = restTemplate.exchange('/api/orders/' + order.id, HttpMethod.PUT, reqEntity, Map)

        // get products
        def id = entityOrder.body.id
        print "after put order " + entityOrder.body

        then:
        entityOrder.statusCode == HttpStatus.OK
        print entityOrder.body
        entityOrder.body.buyersEmail == "beisdog2@web.de"
    }

    def "DELETE /orders/1"() {
        setup:
        def products = new ArrayList<Product>(this.productRepository.findAll())
        def order = new Order(buyersEmail: "beisdog@web.de", products: products)
        order = this.orderRepository.save(order)

        when:
        restTemplate.delete('/api/orders/' + order.id)

        then:
        def entity = restTemplate.getForEntity('/api/orders/', Map)
        print entity.body.content
        entity.body.content.size == 0
    }

    def "POST /orders/1/products"() {
        setup:
        def products = new ArrayList<Product>(this.productRepository.findAll())
        def order = new Order(buyersEmail: "beisdog@web.de")
        order = this.orderRepository.save(order)

        when:
        def productIds = Arrays.asList(products.get(0).sku)
        def entity = restTemplate.postForEntity('/api/orders/' + order.id + "/products/", productIds, Map)
        print "added products " + entity.body
        entity = restTemplate.getForEntity('/api/orders/' + order.id, Map)
        print entity.body

        then:
        entity.body.products.size == 1
    }

    def "DELETE /orders/1/products/1"() {
        setup:
        def products = new ArrayList<Product>(this.productRepository.findAll())
        def order = new Order(buyersEmail: "beisdog@web.de", products: products)
        order = this.orderRepository.save(order)

        when:
        def productId = products.get(0).sku
        def entity = restTemplate.delete('/api/orders/' + order.id + "/products/" + productId)
        entity = restTemplate.getForEntity('/api/orders/' + order.id, Map)
        print entity.body

        then:
        entity.body.products.size == 9
    }

    def "GET /orders/fromTo?from=&to="() {
        setup:
        for (int i = 10; i < 30; i++) {
            def order = new Order(buyersEmail: "beisdog@web.de", orderedAt: dateFormat.parse("2019-10-" + i))
            order = this.orderRepository.save(order)
        }

        when:
        def entity = restTemplate.getForEntity('/api/orders/fromTo?from=2019-10-10&to=2019-10-20', Map)

        then:
        entity.statusCode == HttpStatus.OK
        print entity.body
        entity.body.content.size == 10
    }
}
