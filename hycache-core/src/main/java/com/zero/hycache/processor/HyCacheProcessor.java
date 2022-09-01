package com.zero.hycache.processor;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.util.StringUtils;
import com.zero.hycache.util.TimeUnitUtil;
import com.zero.hycache.util.UuidUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

/**
 * @author zero
 * @date Create on 2022/7/20
 * @description
 */
@SupportedAnnotationTypes("com.zero.hycache.annotation.HyCache")
public class HyCacheProcessor extends AbstractProcessor {

    private final String HYCACHE_METHOD_PROXY_SUFFIX = "$$HyCache";
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Locale locale;
    private TreeMaker treeMaker;
    private JavacTrees trees;
    private Names names;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(HyCache.class)) {
            //1.包名
            HyCache hyCache = element.getAnnotation(HyCache.class);
            //包装类类型
            String fullClassName = element.getEnclosingElement().toString();
            String simpleClassName = fullClassName.split("\\.")[fullClassName.split("\\.").length - 1];
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) trees.getTree(element);
            boolean IS_STATIC_METHOD = false;
            if (jcMethodDecl.getModifiers().getFlags().contains(Modifier.STATIC)) {
                IS_STATIC_METHOD = true;
            }
            // 生成代理后的真实方法
            JCTree.JCMethodDecl newJcMethodDecl = generateRealMethod(jcMethodDecl, IS_STATIC_METHOD);
            // 添加方法到class 中
            addMethod(element, newJcMethodDecl);
            String realMethodName = newJcMethodDecl.name.toString();
            JCTree.JCIdent[] params = new JCTree.JCIdent[newJcMethodDecl.params.size()];
            for (int i = 0; i < newJcMethodDecl.params.size(); i++) {
                params[i] = treeMaker.Ident(newJcMethodDecl.params.get(i).name);
            }
            // 接收HyCache 内容后续使用
            boolean finalIS_STATIC_METHOD = IS_STATIC_METHOD;
            JCTree tree = trees.getTree(element);
            trees.getTree(element).accept(new TreeTranslator() {

                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                    // import package
                    addImportInfo(element, "com.zero.hycache.manager", "HyCacheManager");
                    // 修改body 体内容
                    java.util.List<JCTree.JCStatement> jcStatements = new ArrayList<>();
                    // 需要声明类型
                    //Object cache = HyCacheManager.getCache(key,ex);
                    String cacheKey = StringUtils.isNotBlank(hyCache.key()) ? hyCache.key() : UuidUtils.get32Id();
                    JCTree.JCVariableDecl cacheVar = makeVarDef(
                            treeMaker.Modifiers(0),
                            "cache",
                            memberAccess("java.lang.Object"),
                            treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(
                                            treeMaker.Ident(names.fromString("HyCacheManager")), // . 左边的内容
                                            names.fromString("getCache") // . 右边的内容
                                    ),
                                    List.of(
                                            treeMaker.Literal(cacheKey),
                                            treeMaker.Literal(TimeUnitUtil.getSeconds(hyCache.expire(), hyCache.timeUnit())),
                                            treeMaker.Literal(hyCache.type().toString())
                                    ) // 方法中的内容，多个参数
                            )
                    );
                    // if(cache !=null ) { return cache ;}
                    JCTree.JCIf ifStatement = treeMaker.If(
                            // if 判断条件
                            treeMaker.Binary(
                                    JCTree.Tag.NE,
                                    treeMaker.Ident(getNameFromString("cache")),
                                    treeMaker.Literal(TypeTag.BOT, null)
                            ),
                            // 条件满足执行
                            treeMaker.Return(treeMaker.TypeCast(jcMethodDecl.restype, treeMaker.Ident(names.fromString("cache")))),
                            // 条件不满足执行
                            null
                    );
                    // Object result = this.$method($param);
                    JCTree.JCVariableDecl resultVar = makeVarDef(
                            treeMaker.Modifiers(0), // 访问限制类型
                            "result",   // 申明的值
                            memberAccess("java.lang.Object"), // 申明值的类型
                            treeMaker.Exec(
                                    treeMaker.Apply(
                                            List.nil(),
                                            treeMaker.Select(
                                                    finalIS_STATIC_METHOD ? treeMaker.Ident(names.fromString(simpleClassName)) : treeMaker.Ident(names.fromString("this")), // . 左边的内容
                                                    names.fromString(realMethodName) // . 右边的内容
                                            ),
                                            com.sun.tools.javac.util.List.from(params) // 方法中的内容，多个参数
                                    )
                            ).expr
                    );
                    // addCache
                    JCTree.JCExpressionStatement addCache = treeMaker.Exec(treeMaker.Apply(
                            List.nil(),
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString("HyCacheManager")), // . 左边的内容
                                    names.fromString("addCache") // . 右边的内容
                            ),
                            // 调用方法传参
                            List.of(
                                    treeMaker.Literal(cacheKey),
                                    treeMaker.Literal(TimeUnitUtil.getSeconds(hyCache.expire(), hyCache.timeUnit())),
                                    treeMaker.Literal(hyCache.type().toString()),
                                    treeMaker.Ident(names.fromString("result"))
                            )
                    ));
                    // 返回结果
                    JCTree.JCReturn returnRes = treeMaker.Return(treeMaker.TypeCast(jcMethodDecl.restype, treeMaker.Ident(names.fromString("result"))));
                    jcStatements.add(cacheVar);
                    jcStatements.add(ifStatement);
                    jcStatements.add(resultVar);
                    jcStatements.add(addCache);
                    jcStatements.add(returnRes);
                    // 调用真实的方法
                    jcMethodDecl.body.stats = List.from(jcStatements);
                    super.visitMethodDef(jcMethodDecl);
                }
            });
        }
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        locale = processingEnv.getLocale();
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.trees = JavacTrees.instance(processingEnv);
        this.names = Names.instance(context);

    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }


    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    private JCTree.JCMethodDecl generateRealMethod(JCTree.JCMethodDecl jcMethodDecl, boolean IS_STATIC_METHOD) {

        JCTree.JCModifiers jcModifiers = treeMaker.Modifiers(Flags.PRIVATE);
        // support static method
        if (IS_STATIC_METHOD) {
            jcModifiers=treeMaker.Modifiers(Flags.STATIC);
        }
        JCTree.JCBlock body = jcMethodDecl.body;
        List<JCTree.JCStatement> statements = body.getStatements();
        JCTree.JCBlock bodyBlock = treeMaker.Block(0, statements);
        //构建方法
        return treeMaker
                .MethodDef(jcModifiers, names.fromString(jcMethodDecl.name + HYCACHE_METHOD_PROXY_SUFFIX), jcMethodDecl.restype, jcMethodDecl.typarams, jcMethodDecl.params, jcMethodDecl.thrown, bodyBlock, null);
    }

    private void addImportInfo(Element element, String importPackageName, String className) {
        TreePath treePath = trees.getPath(element);
        Tree leaf = treePath.getLeaf();
        if (treePath.getCompilationUnit() instanceof JCTree.JCCompilationUnit && leaf instanceof JCTree) {
            JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();

            for (JCTree jcTree : jccu.getImports()) {
                if (jcTree instanceof JCTree.JCImport) {
                    JCTree.JCImport jcImport = (JCTree.JCImport) jcTree;
                    if (jcImport.qualid instanceof JCTree.JCFieldAccess) {
                        JCTree.JCFieldAccess jcFieldAccess = (JCTree.JCFieldAccess) jcImport.qualid;
                        try {
                            if (importPackageName.equals(jcFieldAccess.selected.toString()) && className.equals(jcFieldAccess.name.toString())) {
                                return;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            java.util.List<JCTree> trees = new ArrayList<>(jccu.defs);
            JCTree.JCIdent ident = treeMaker.Ident(names.fromString(importPackageName));
            JCTree.JCImport jcImport = treeMaker.Import(treeMaker.Select(
                    ident, names.fromString(className)), false);
            if (!trees.contains(jcImport)) {
                trees.add(0, jcImport);
            }
            jccu.defs = List.from(trees);
        }
    }

    // TODO 内部类的方式可能有问题，后续进行优化
    private void addMethod(Element element, JCTree.JCMethodDecl jcMethodDecl) {
        TreePath treePath = trees.getPath(element);
        Tree leaf = treePath.getLeaf();
        if (treePath.getCompilationUnit() instanceof JCTree.JCCompilationUnit && leaf instanceof JCTree) {
            JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
            List<JCTree> jcTrees = jccu.defs;
            for (JCTree jcTree : jcTrees) {
                if (jcTree instanceof JCTree.JCClassDecl) {
                    JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) jcTree;
                    java.util.List<JCTree> trees = new ArrayList<>(jcClassDecl.defs);
                    trees.add(jcMethodDecl);
                    jcClassDecl.defs = List.from(trees);
                }
            }
        }
    }

    private JCTree.JCAnnotation makeAnnotation(String annotaionName, List<JCTree.JCExpression> args) {
        JCTree.JCExpression expression = chainDots(annotaionName.split("\\."));
        JCTree.JCAnnotation jcAnnotation = treeMaker.Annotation(expression, args);
        return jcAnnotation;
    }

    private JCTree.JCImport buildImport(String packageName, String className) {
        JCTree.JCIdent ident = treeMaker.Ident(names.fromString(packageName));
        JCTree.JCImport jcImport = treeMaker.Import(treeMaker.Select(
                ident, names.fromString(className)), false);
        return jcImport;
    }

    // TODO 考虑提取到通用的类中
    private JCTree.JCVariableDecl makeVarDef(JCTree.JCModifiers modifiers, String name, JCTree.JCExpression vartype, JCTree.JCExpression init) {
        return treeMaker.VarDef(
                modifiers,
                getNameFromString(name), //名字
                vartype, //类型
                init //初始化语句
        );
    }

    public JCTree.JCExpression chainDots(String... elems) {
        assert elems != null;

        JCTree.JCExpression e = null;
        for (int i = 0; i < elems.length; i++) {
            e = e == null ? treeMaker.Ident(names.fromString(elems[i])) : treeMaker.Select(e, names.fromString(elems[i]));
        }
        assert e != null;

        return e;
    }
}
