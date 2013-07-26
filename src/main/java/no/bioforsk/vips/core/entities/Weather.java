package no.bioforsk.vips.core.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author treinar
 */
//@XmlRootElement(name="weather")
//@XmlAccessorType(XmlAccessType.FIELD)
public class Weather {
    
    public Weather()
    {
        
    }
    
    public Weather(Date date, String details){
        this.setDate(date);
        this.setDetails(details);
    }
    
    //@XmlAttribute
    private Date date;
    
    //@XmlElement
    private String details;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }
}
