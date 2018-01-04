package dev.springboot.entity.model;

import dev.springboot.annotation.Attribute;
import dev.springboot.annotation.ObjectType;
import dev.springboot.entity.attr.AdminAttr;

import java.lang.reflect.Field;
import java.util.HashMap;

@ObjectType(AdminAttr.OBJTYPE)
public class Admin extends User
{
    public interface Model extends AdminAttr
    {                       }

    public void fillAttributeFields(HashMap<String, Object> hashMap)
    {
        super.fillAttributeFields(hashMap);

        Field sqcField[] = Admin.class.getDeclaredFields();
        Attribute attrib;
        int length = sqcField.length;
        try
        {
            for (int i = 0; i < length; ++i)
            {
                attrib = sqcField[i].getAnnotation(Attribute.class);

                if (attrib != null)
                    sqcField[i].set(this, hashMap.get(attrib.value()));
            }
        }

        catch (IllegalAccessException exc)
        {
            exc.printStackTrace();
        }
    }

    public HashMap getAllFields()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "Admin{"
                + super.toString()
                + "}";
    }
}
