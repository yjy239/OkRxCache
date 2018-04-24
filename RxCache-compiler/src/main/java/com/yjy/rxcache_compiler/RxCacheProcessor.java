package com.yjy.rxcache_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.yjy.okexcache_base.AutoCache;
import com.yjy.okexcache_base.LifeCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@AutoService(Processor.class)
public class RxCacheProcessor extends AbstractProcessor{

    private Messager mMessager;
    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annonations = new LinkedHashSet<>();
//        annonations.add(LifeCache.class.getCanonicalName());
        annonations.add(AutoCache.class.getCanonicalName());
        return annonations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //方法参数

        for(Element e : roundEnvironment.getElementsAnnotatedWith(AutoCache.class)){

            System.out.println("location : "+e.getEnclosingElement()+"."+e.getSimpleName());
            List<Symbol.MethodSymbol> methods = getMethodSymbol(e);


        }
        return false;
    }

    //获取方法所有信息
    private List<Symbol.MethodSymbol> getMethodSymbol(Element e){
        List<Symbol.MethodSymbol> methods = new ArrayList<>();
        List<? extends Element> elements = e.getEnclosedElements();
        for(Element child : elements){
            if(child.getKind() ==  ElementKind.METHOD){
                System.out.println("------->"+"\n");
                Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) child;
                RxCacheMethod method = new RxCacheMethod();
                String nameMethod = methodSymbol.getSimpleName().toString();
                System.out.println("name: "+nameMethod+" return"+methodSymbol.getReturnType()+"\n");
                System.out.println("getAnnotationMirrors: "+methodSymbol.getAnnotationMirrors()+"\n");
                System.out.println("getParameters "+methodSymbol.getParameters()+"\n");
                method.setmMethod(methodSymbol);
                method.setmAnnonations(getAnnations(methodSymbol.getAnnotationMirrors()));
                //扫描方法参数中的
                for(Symbol.VarSymbol var : methodSymbol.getParameters()){
//                    System.out.println("var "+var.getSimpleName()+" "+var.getAnnotationMirrors()+"\n");
                    for(Attribute.Compound compound : var.getAnnotationMirrors()){
                        System.out.println( "var "+var.getSimpleName()+" compound.getElementValues "+compound.getElementValues()+" getAnnotationType "+compound.getAnnotationType());

                    }

                }
                System.out.println("getModifiers "+methodSymbol.getModifiers()+"\n");
                System.out.println("getReceiverType "+methodSymbol.getReceiverType()+"\n");
                methods.add(methodSymbol);

                System.out.println("------->"+"\n");
            }

        }
        return methods;
    }

    public List<RxCacheAnnonation> getAnnations(List<Attribute.Compound> compounds){
        List<RxCacheAnnonation> annonations = new ArrayList<>();
        for(Attribute.Compound compound : compounds){
            RxCacheAnnonation annonation = new RxCacheAnnonation();
            String wholeName = compound.getAnnotationType().toString();
            ClassName annationClassName = getClassName(wholeName);
            AnnotationSpec.Builder specBuilder = AnnotationSpec.builder(annationClassName);
            //注解有多少个注解数值
            System.out.println(compound.getElementValues());
            for(Map.Entry<Symbol.MethodSymbol,Attribute> attribute : compound.getElementValues().entrySet()){

                System.out.println("key "+attribute.getKey()+"value "+attribute.getValue());
                specBuilder = buildAnninationValue(specBuilder,attribute);
//                specBuilder.addMember(attribute.getKey().toString(),"$S",attribute.getValue());
            }
            annonation.setSpec(specBuilder.build());
            annonations.add(annonation);
        }
        return annonations;
    }


    //根据判断添加
    private AnnotationSpec.Builder buildAnninationValue(AnnotationSpec.Builder specBuilder,
                                                        Map.Entry<Symbol.MethodSymbol,Attribute> attribute){

        switch (attribute.getKey().toString()){
            case "duaration()":
                specBuilder.addMember(attribute.getKey().toString(),"$L",attribute.getValue());
                break;
            case "unit()":
                ClassName unitName = getClassName(attribute.getValue().toString());
                specBuilder.addMember(attribute.getKey().toString(),"$T",unitName);
                break;
            case "setFromNet()":
                specBuilder.addMember(attribute.getKey().toString(),"$L",attribute.getValue());
                break;
            case "value()":
                specBuilder.addMember(attribute.getKey().toString(),"$S",attribute.getValue());
                break;
        }


        return specBuilder;
    }


    private ClassName getClassName(String wholeName){
        int lastDot = wholeName.lastIndexOf('.');
        String packageName = wholeName.substring(0,lastDot);
        String SimpleName = wholeName.substring(lastDot+1,wholeName.length());
        System.out.println("packageName "+packageName+" SimpleName "+SimpleName);
        return ClassName.get(packageName,SimpleName);
    }


    //创建所有方法集合
    private List<MethodSpec> getMethodSpecs( List<Symbol.MethodSymbol> methodSymbols){
        List<MethodSpec> mMethodSpecs = new ArrayList<>();
        for(Symbol.MethodSymbol methodSymbol : methodSymbols){
//            MethodSpec.methodBuilder(methodSymbol.getSimpleName().toString())
//                    .addModifiers(Modifier.PUBLIC)
//                    .addAnnotations().build();
    }
        return mMethodSpecs;
    }




    private void write(){

    }
}
