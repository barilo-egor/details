package tgb.cryptoexchange.details.service;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Modifying;
import tgb.cryptoexchange.details.entity.BasePersist;
import tgb.cryptoexchange.details.exception.BaseException;
import tgb.cryptoexchange.details.interfaces.IBasePersistService;
import tgb.cryptoexchange.details.repository.BaseRepository;

import java.util.Collection;
import java.util.List;

public abstract class BasePersistService<T extends BasePersist> implements IBasePersistService<T> {

    protected abstract BaseRepository<T> getBaseRepository();

    public T findById(Long pid) {
        return getBaseRepository().findById(pid).orElseThrow(
                () -> new BaseException("Запись по pid " + pid + " не найдена."));
    }

    public void deleteById(Long pid) {
        getBaseRepository().deleteById(pid);
    }

    public void delete(T t) {
        getBaseRepository().delete(t);
    }

    public void deleteAll() {
        getBaseRepository().deleteAll();
    }

    public void deleteAll(Collection<T> collection) {
        getBaseRepository().deleteAll(collection);
    }

    @Modifying
    public T save(T t) {
        return getBaseRepository().save(t);
    }

    public List<T> findAll() {
        return getBaseRepository().findAll();
    }

    public List<T> findAll(Example<T> example) {
        return getBaseRepository().findAll(example);
    }

    public boolean existsById(Long pid) {
        return getBaseRepository().existsById(pid);
    }

    public long count(Example<T> example) {
        return getBaseRepository().count(example);
    }

    public long count() {
        return getBaseRepository().count();
    }
}