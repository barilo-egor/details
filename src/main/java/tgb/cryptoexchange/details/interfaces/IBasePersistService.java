package tgb.cryptoexchange.details.interfaces;

import org.springframework.data.domain.Example;
import tgb.cryptoexchange.details.entity.BasePersist;

import java.util.Collection;
import java.util.List;

public interface IBasePersistService<T extends BasePersist> {

    T findById(Long pid);

    void deleteById(Long pid);

    void delete(T t);

    void deleteAll();

    void deleteAll(Collection<T> collection);

    T save(T t);

    List<T> findAll();

    List<T> findAll(Example<T> example);

    long count(Example<T> example);

    long count();
}
