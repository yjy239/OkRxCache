package com.yjy.rxcache_compiler;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.yjy.okrxcache_base.AutoCache;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
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
@SupportedAnnotationTypes("*")
public class RxCacheProcessor extends AbstractProcessor{

    private Messager mMessager;
    private Filer filer;
    private int tally;
    private Trees trees;
    private TreeMaker make;
    private Name.Table names;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        trees = Trees.instance(processingEnvironment);
        Context context = ((JavacProcessingEnvironment)
                processingEnvironment).getContext();
        make = TreeMaker.instance(context);
        names = Names.instance(context).table;//Name.Table.instance(context);
        tally = 0;

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


//            List<RxCacheMethod> methods = getMethodSymbol(e);
            if(e.getKind() == ElementKind.INTERFACE){
                System.out.println("location : "+e.getEnclosingElement()+"."
                        +e.getSimpleName()+"\n"+e.getKind());
                createResponseFounction(e);
            }else {
                System.out.println("Attention!!! @AutoCache only use on Interface");
            }


        }
        return false;
    }


    private void createResponseFounction(Element element){
        System.out.println("--------------------> create start");
        JCTree jcTree = (JCTree) trees.getTree(element);

        jcTree.accept(new ResponseTree());

        System.out.println("--------------------> create end");
    }



    private class ResponseTree extends TreeTranslator{

        @Override
        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
            jcClassDecl.mods = (JCTree.JCModifiers) this.translate((JCTree) jcClassDecl.mods);
            jcClassDecl.typarams = this.translateTypeParams(jcClassDecl.typarams);
            jcClassDecl.extending = (JCTree.JCExpression) this.translate((JCTree) jcClassDecl.extending);
            jcClassDecl.implementing = this.translate(jcClassDecl.implementing);

            ListBuffer<JCTree> statements = new ListBuffer<>();
            List<JCTree> oldList = this.translate(jcClassDecl.defs);


            for (JCTree jcTree : oldList) {

                //模仿lombok的文件体分析
                if(jcTree instanceof JCTree.JCMethodDecl){
                    JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) jcTree;
//                    System.out.println(method);
//                    System.out.println(method.restype);
                    JCTree.JCExpression observerExp = make.Select(make.Ident(names.fromString("io.reactivex")),names.fromString("Observable"));
//                    System.out.println(" id "+observerExp);
                    //retrofit2.Response
                    JCTree.JCExpression ResponseExp = make.Select(make.Ident(names.fromString("retrofit2")),names.fromString("Response"));
                    //okhttp3.ResponseBody
                    JCTree.JCExpression ResponseBodyExp = make.Select(make.Ident(names.fromString("okhttp3")),names.fromString("ResponseBody"));
                    JCTree.JCExpression returnExp = make.TypeApply(ResponseExp, com.sun.tools.javac.util.List.<JCTree.JCExpression>of(ResponseBodyExp));
                    JCTree.JCExpression resultExp = make.TypeApply(observerExp,com.sun.tools.javac.util.List.<JCTree.JCExpression>of(returnExp));
                    JCTree.JCMethodDecl methodDecl = make.MethodDef(method.getModifiers(),
                            names.fromString(method.getName()+"$$proxy"),
                            resultExp,
                            method.getTypeParameters(),
                            method.getParameters(),
                            method.getThrows(),
                            method.getBody(),
                            method.defaultValue);

                    statements.append(methodDecl);
                }

                statements.append(jcTree);
            }

            jcClassDecl.defs = statements.toList(); //更新

        }
    }


}
