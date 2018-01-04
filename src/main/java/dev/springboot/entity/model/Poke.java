package dev.springboot.entity.model;

import dev.springboot.annotation.Attribute;
import dev.springboot.annotation.ObjectType;
import dev.springboot.entity.attr.PokeAttr;

import java.lang.reflect.Field;
import java.util.HashMap;

@ObjectType(PokeAttr.OBJTYPE)
public class Poke extends User
{
    public interface Model extends PokeAttr
    {                       }

    @Override
    public void fillAttributeFields(HashMap<String, Object> hashMap)
    {
        super.fillAttributeFields(hashMap);

        Field sqcField[] = Poke.class.getDeclaredFields();
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

    @Override
    public HashMap getAllFields()
    {
        HashMap<String, Object> hashmap = super.getAllFields();

        return hashmap;
    }

    @Override
    public String toString()
    {
        return "Poke{"
                + super.toString()
                + "}";
    }
}
