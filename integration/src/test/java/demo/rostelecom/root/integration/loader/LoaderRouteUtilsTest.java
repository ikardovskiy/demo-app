package demo.rostelecom.root.integration.loader;

import demo.rostelecom.root.model.PhoneCode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static demo.rostelecom.root.integration.loader.LoaderRouteUtils.convertToPhoneCodesList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderRouteUtilsTest {

    public static final PhoneCode PHONE_CODE_1 = PhoneCode.builder()
            .code("007")
            .name("A")
            .country("A1")
            .build();

    public static final PhoneCode PHONE_CODE_2 = PhoneCode.builder()
            .code(null)
            .name("B")
            .country("B1")
            .build();


    @Test
    public void test_convertToPhoneCodesList() {

        Map<String, String> names = Map.of("BD", "Bangladesh", "BE", "Belgium");
        Map<String, String> phones = Map.of("BD", "880", "BE", "32");

        final var phoneCodeList = convertToPhoneCodesList(names, phones);

        var expectedPhoneCodeList = List.of(
                PhoneCode.builder().name("BD").country("Bangladesh").code("880").build(),
                PhoneCode.builder().name("BE").country("Belgium").code("32").build()
        );

        assertTrue(phoneCodeList.size() == 2);
        assertTrue(phoneCodeList.contains(expectedPhoneCodeList.get(0)));
        assertTrue(phoneCodeList.contains(expectedPhoneCodeList.get(1)));
    }

    @Test
    public void test_calculateDeletedPhoneCodes(){

        List<PhoneCode> loaded = List.of(PHONE_CODE_1);
        List<PhoneCode> stored = List.of(PHONE_CODE_1,PHONE_CODE_2);

        final var deleted = LoaderRouteUtils.calculateDeletedPhoneCodes(loaded, stored);

        final var expected = List.of(PHONE_CODE_2);

        assertEquals(expected,deleted);
    }
}