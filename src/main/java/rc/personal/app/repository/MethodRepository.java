package rc.personal.app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import rc.personal.app.model.Method;

public interface MethodRepository extends MongoRepository<Method, String> {

    public Method findByFileName(String firstName);

}
