
package eu.hyvar.reconfigurator.output.translation.format;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Final configuration
 * 
 */

public class OutputOfHyVarRec {

    /**
     * The result may be sat or unsat
     * (Required)
     * 
     */
    @SerializedName("result")
    @Expose
    private String result;
    /**
     * list representing which features are selected
     * 
     */
    @SerializedName("features")
    @Expose
    private List<String> features = null;
    /**
     * list representing the values of the attributes
     * 
     */
    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes = null;

    /**
     * The result may be sat or unsat
     * (Required)
     * 
     * @return
     *     The result
     */
    public String getResult() {
        return result;
    }

    /**
     * The result may be sat or unsat
     * (Required)
     * 
     * @param result
     *     The result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * list representing which features are selected
     * 
     * @return
     *     The features
     */
    public List<String> getFeatures() {
        return features;
    }

    /**
     * list representing which features are selected
     * 
     * @param features
     *     The features
     */
    public void setFeatures(List<String> features) {
        this.features = features;
    }

    /**
     * list representing the values of the attributes
     * 
     * @return
     *     The attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * list representing the values of the attributes
     * 
     * @param attributes
     *     The attributes
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

}
