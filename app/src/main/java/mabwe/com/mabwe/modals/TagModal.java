package mabwe.com.mabwe.modals;

import java.io.Serializable;

/**
 * Created by mindiii on 5/7/18.
 */

public class TagModal  implements Serializable{
    public String tagId = "";
    public String tagName = "";
    public  boolean istagSelecte = false;

    @Override
    public String toString() {
        return tagName;
    }
}
