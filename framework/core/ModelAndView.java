package framework.core;

import java.util.HashMap;

public class ModelAndView {
    
    private String pageName;
    private HashMap<String, Object> attributes;

    public ModelAndView(String pageName, HashMap<String, Object> attributes){
        this.pageName = pageName;
        this.attributes = attributes;
    }

    public String getPageName() {
        return pageName;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public void addAttribute(String attributeName, Object data){
        this.attributes.put(attributeName, data);
    }

}
