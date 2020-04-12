package demo.rostelecom.root.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "phonecodes")
public class PhoneCode {

    /**
     * country code
     */
    @Id
    private String name;
    /**
     * country name
     */
    private String country;
    /**
     * phone code
     */
    private String code;

/*    //@JsonIgnore
    @Version
    private Long version;

    //@JsonIgnore
    @Score
    private float score;*/
}
