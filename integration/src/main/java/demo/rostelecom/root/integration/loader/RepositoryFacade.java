package demo.rostelecom.root.integration.loader;

import demo.rostelecom.root.model.PhoneCode;

import java.util.List;

public interface RepositoryFacade {
    void save(List<PhoneCode> codes);
    void delete(List<PhoneCode> codes);
    List<PhoneCode> getAll();
}
