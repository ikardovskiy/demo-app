package demo.rostelecom.root.integration.loader;

import demo.rostelecom.root.model.PhoneCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoaderRouteUtils {

    public static List<PhoneCode> convertToPhoneCodesList(Map<String, String> names, Map<String, String> phones){
        var phoneCodeList = names.entrySet().stream().map(entry -> PhoneCode.builder()
                .name(entry.getKey())
                .country(entry.getValue())
                .code(phones.get(entry.getKey()))
                .build()).collect(Collectors.toList());
        return phoneCodeList;
    }

    public static List<PhoneCode> calculateDeletedPhoneCodes(List<PhoneCode> loadedPhones,List<PhoneCode> storedPhones){
        final var loadedNames = loadedPhones.stream()
                .map(PhoneCode::getName)
                .collect(Collectors.toSet());

        var deletedPhoneCodes = storedPhones.stream()
                .filter(s->!loadedNames.contains(s.getName()))
                .collect(Collectors.toList());

        return deletedPhoneCodes;
    }

}
