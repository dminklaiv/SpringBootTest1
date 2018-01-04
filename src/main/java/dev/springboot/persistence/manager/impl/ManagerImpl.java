package dev.springboot.persistence.manager.impl;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.pool.OracleDataSource;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dev.springboot.annotation.ObjectType;
import dev.springboot.entity.model.Admin;
import dev.springboot.entity.model.BaseEntity;
import dev.springboot.entity.model.Master;
import dev.springboot.entity.model.Poke;
import dev.springboot.persistence.PersistenceEntity;
import dev.springboot.persistence.converter.impl.ConverterImpl;
import dev.springboot.persistence.manager.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

//import static dev.springboot.OracleConnector.getConnection;

@Component
public class ManagerImpl implements Manager
{
/*::|       FIELD       :~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:*/
    @Autowired
    private OracleDataSource dataSource;
/*::|       CONSTRUCTOR       :~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:*/
/*::|       SUB_CLASS       :~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:*/
/*::|       F / P       :~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:~:*/
    public void createEntity
    (
        PersistenceEntity persistenceEntity,
        final Class<? extends BaseEntity> CLASS
    )
    {
        try
        {
            HashMap<String, Object> hashMap =
                        (HashMap<String, Object>) persistenceEntity.getAttributes();
            String[] elements = new String[4 + (hashMap.size() << 1)];
            elements[0] = Long.toString(persistenceEntity.getObject_id());
            elements[1] = CLASS.getAnnotation(ObjectType.class).value();
            elements[2] = persistenceEntity.getName();
            elements[3] = persistenceEntity.getDescription();

            int i = 4;
            Iterator<String> iterator = hashMap.keySet().iterator();
            while (iterator.hasNext())
            {
                String attrID = iterator.next();
                elements[i] = attrID;
                ++i;
                elements[i] = ConverterImpl.convertObjectToString(hashMap.get(attrID));
                ++i;
            }

            Connection connection = dataSource.getConnection();
            //Connection connection = getConnection();
            ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor
                                                (
                                                    "ARRAY",
                                                    connection
                                                );
            ARRAY array = new ARRAY(descriptor, connection, elements);

            OracleCallableStatement stmt = (OracleCallableStatement)connection.prepareCall
                                            (
                                                INSERT_ENTITY
                                            );
            stmt.setARRAY(1, array);
            stmt.execute();
        }

        catch (SQLException exc)
        {
            exc.printStackTrace();
        }
    }


    public PersistenceEntity getEntity(long id, final Class<? extends BaseEntity> CLASS)
    {
        OracleCallableStatement calStat;
        ResultSet resultSet;
        PersistenceEntity persistenceEntity = new PersistenceEntity();
        try
        {
            //Connection connection = getConnection();//
            Connection connection = dataSource.getConnection();
            calStat = (OracleCallableStatement) connection.prepareCall(GET_ENTITY);
            calStat.setString(1, Long.toString(id));
            calStat.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
            calStat.execute();
            resultSet = calStat.getCursor(2);

            persistenceEntity.setObject_id(id);
            HashMap<String, Object> attrMap = new HashMap<>();
            while (resultSet.next()) {
                String attr_id = resultSet.getString(2);

                switch (attr_id)
                {
                    case ATTR_NAME :
                        persistenceEntity.setName(resultSet.getString(1));
                        break;

                    case ATTR_DESCR :
                        persistenceEntity.setDescription(resultSet.getString(1));
                        break;

                    default:
                        Class fieldType = BaseEntity.getFieldType(attr_id, CLASS);
                        Object fieldObj = ConverterImpl.convertStringToObject
                                    (
                                        resultSet.getString(1),
                                        fieldType
                                    );
                        attrMap.put(attr_id, fieldObj);
                }
            }

            persistenceEntity.setAttributes(attrMap);
        }

        catch (SQLException | ParseException exc)
        {
            //exc.printStackTrace();
            return null;
        }

        return persistenceEntity;
    }

    public PersistenceEntity getUser(String phoneNumber, String password)
    {
        OracleCallableStatement calStat;
        ResultSet resultSet;
        PersistenceEntity persistenceEntity = new PersistenceEntity();
        try
        {
            Connection connection = dataSource.getConnection();//dataSource.getConnection();getConnection()
            calStat = (OracleCallableStatement) connection.prepareCall(GET_USER);
            calStat.setString(1, phoneNumber);
            calStat.setString(2, password);
            calStat.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
            calStat.execute();
            resultSet = calStat.getCursor(3);

            HashMap<String, Object> attrMap = new HashMap<>();
            Class<? extends BaseEntity> entityClassType = null;
            final String MASTER_OT = Master.class.getAnnotation(ObjectType.class).value();
            final String POKE_OT = Poke.class.getAnnotation(ObjectType.class).value();
            while (resultSet.next())
            {
                String attr_id = resultSet.getString(2);

                switch (attr_id)
                {
                    case ATTR_OBJECT_ID :
                        persistenceEntity.setObject_id(Long.parseLong(resultSet.getString(1)));
                    break;

                    case ATTR_OBJECT_TYPE_ID :
                        String objectTypeID = resultSet.getString(1);

                        if (objectTypeID.equals(MASTER_OT))
                            entityClassType = Master.class;

                        else if (objectTypeID.equals(POKE_OT))
                            entityClassType = Poke.class;

                        else
                            entityClassType = Admin.class;

                        persistenceEntity.setClassType(entityClassType);
                    break;

                    case ATTR_NAME :
                        persistenceEntity.setName(resultSet.getString(1));
                    break;

                    case ATTR_DESCR :
                        persistenceEntity.setDescription(resultSet.getString(1));
                    break;

                    default:
                        Class fieldType = BaseEntity.getFieldType(attr_id, entityClassType);
                        Object fieldObj = ConverterImpl.convertStringToObject
                                (
                                    resultSet.getString(1),
                                    fieldType
                                );
                        attrMap.put(attr_id, fieldObj);
                }
            }

            persistenceEntity.setAttributes(attrMap);
        }

        catch (SQLException | ParseException exc)
        {
            //exc.printStackTrace();
            return null;
        }

        return persistenceEntity;
    }

    public void deleteEntity(long id)
    {
     //Connection connection = dataSource.getConnection();???????????

        try
        {   Connection connection = dataSource.getConnection(); //??????????
            PreparedStatement statement = connection.prepareStatement(DELETE_ENTITY);
            statement.setString(1, Long.toString(id));
            statement.execute();
        }

        catch (SQLException exc)
        {
            exc.printStackTrace();
        }
    }

    public void updateEntity
    (
        PersistenceEntity persistenceEntity,
        final Class<? extends BaseEntity> CLASS
    )
    {

    }
}
