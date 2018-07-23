package com.yjy.okrxcache.test;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/07/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class TestTypeAdapter extends TypeAdapter<ITest> {

    private Gson gson;
    private Excluder excluder = Excluder.DEFAULT;
    private FieldNamingPolicy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
    private ConstructorConstructor constructorConstructor = new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap());
    private Map<String, BoundField> boundFields;


    public TestTypeAdapter(){
        gson = new Gson();
    }

    @Override
    public void write(JsonWriter out, ITest value) throws IOException {
        Log.e("write",value+"");


        if (value == null) {
            out.nullValue();
            return;
        }


        out.beginObject();
        out.name("clazz").value(value.getClazz().getName());
        TypeToken token = TypeToken.getParameterized((Type) value.getClass());
        boundFields = getBoundFields(gson,token,token.getRawType());
        try {
            for (BoundField boundField : boundFields.values()) {
                if (boundField.writeField(value)) {
                    out.name(boundField.name);
                    boundField.write(out, value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
        out.endObject();


    }

    @Override
    public ITest read(JsonReader in) throws IOException {
        Log.e("read","");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        ITest iTest = null;

        try {
            in.beginObject();
            String clazz = in.nextName();
            if(clazz.equals("clazz")){
                String clazzName = in.nextString();
                Log.e("clazz",clazzName);
                try {
                    iTest = (ITest) Class.forName(clazzName).newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            if(iTest == null){
                return null;
            }
            TypeToken token = TypeToken.getParameterized((Type) iTest.getClass());
            boundFields = getBoundFields(gson,token,token.getRawType());
            while (in.hasNext()) {
                String name = in.nextName();
                BoundField field = boundFields.get(name);
                if (field == null || !field.deserialized) {
                    in.skipValue();
                } else {
                    field.read(in, iTest);
                }
            }
        } catch (IllegalStateException e) {
            throw new JsonSyntaxException(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
        in.endObject();


        return iTest;
    }

    public boolean excludeField(Field f, boolean serialize) {
        return excludeField(f, serialize, excluder);
    }

    static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
        return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
    }

    /** first element holds the default name */
    private List<String> getFieldNames(Field f) {
        SerializedName annotation = f.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = fieldNamingPolicy.translateName(f);
            return Collections.singletonList(name);
        }

        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }

        List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
        fieldNames.add(serializedName);
        for (String alternate : alternates) {
            fieldNames.add(alternate);
        }
        return fieldNames;
    }

    private BoundField createBoundField(
            final Gson context, final Field field, final String name,
            final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        // special casing primitives here saves ~5% on Android...
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = getTypeAdapter(
                    constructorConstructor, context, fieldType, annotation);
        }
        final boolean jsonAdapterPresent = mapped != null;
        if (mapped == null) mapped = context.getAdapter(fieldType);

        final TypeAdapter<?> typeAdapter = mapped;
        return new BoundField(name, serialize, deserialize) {
            @SuppressWarnings({"unchecked", "rawtypes"}) // the type adapter and field type always agree
            @Override void write(JsonWriter writer, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = field.get(value);
                TypeAdapter t = jsonAdapterPresent ? typeAdapter
                        : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
                t.write(writer, fieldValue);
            }
            @Override void read(JsonReader reader, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }
            @Override public boolean writeField(Object value) throws IOException, IllegalAccessException {
                if (!serialized) return false;
                Object fieldValue = field.get(value);
                return fieldValue != value; // avoid recursion for example for Throwable.cause
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }

        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                    continue;
                }
                field.setAccessible(true);
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = getFieldNames(field);
                BoundField previous = null;
                for (int i = 0, size = fieldNames.size(); i < size; ++i) {
                    String name = fieldNames.get(i);
                    if (i != 0) serialize = false; // only serialize the default name
                    BoundField boundField = createBoundField(context, field, name,
                            TypeToken.get(fieldType), serialize, deserialize);
                    BoundField replaced = result.put(name, boundField);
                    if (previous == null) previous = replaced;
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                            + " declares multiple JSON fields named " + previous.name);
                }
            }

            if(raw == null){
                Log.e("raw","null");
            }

            if(raw.getGenericSuperclass() == null){
                Log.e("raw","null");
            }

            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson,
                                  TypeToken<?> type, JsonAdapter annotation) {
        Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();

        TypeAdapter<?> typeAdapter;
        if (instance instanceof TypeAdapter) {
            typeAdapter = (TypeAdapter<?>) instance;
        } else if (instance instanceof TypeAdapterFactory) {
            typeAdapter = ((TypeAdapterFactory) instance).create(gson, type);
        } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
            JsonSerializer<?> serializer = instance instanceof JsonSerializer
                    ? (JsonSerializer) instance
                    : null;
            JsonDeserializer<?> deserializer = instance instanceof JsonDeserializer
                    ? (JsonDeserializer) instance
                    : null;
            typeAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null);
        } else {
            throw new IllegalArgumentException("Invalid attempt to bind an instance of "
                    + instance.getClass().getName() + " as a @JsonAdapter for " + type.toString()
                    + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory,"
                    + " JsonSerializer or JsonDeserializer.");
        }

        if (typeAdapter != null && annotation.nullSafe()) {
            typeAdapter = typeAdapter.nullSafe();
        }

        return typeAdapter;
    }

    static abstract class BoundField {
        final String name;
        final boolean serialized;
        final boolean deserialized;

        protected BoundField(String name, boolean serialized, boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }
        abstract boolean writeField(Object value) throws IOException, IllegalAccessException;
        abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;
        abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;
    }
}
