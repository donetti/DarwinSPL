
package eu.hyvar.feature.exporter.hfm_exporter.rest.input;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class FeatureModel {

    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("specification")
    @Expose
    private String specification;

    /**
     * 
     * @return
     *     The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 
     * @param filename
     *     The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 
     * @return
     *     The specification
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 
     * @param specification
     *     The specification
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

}
