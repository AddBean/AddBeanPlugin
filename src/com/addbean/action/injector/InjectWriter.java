package com.addbean.action.injector;

import com.addbean.utils.common.Definitions;
import com.addbean.utils.common.Utils;
import com.addbean.utils.model.Element;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;

public class InjectWriter extends WriteCommandAction.Simple {

    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected ArrayList<Element> mElements;
    protected PsiElementFactory mFactory;
    protected String mLayoutFileName;
    protected String mFieldNamePrefix;
    protected boolean mCreateHolder;
    protected boolean mSplitOnclickMethods;

    public InjectWriter(PsiFile file, PsiClass clazz, String command, ArrayList<Element> elements, String layoutFileName, String fieldNamePrefix, boolean createHolder, boolean splitOnclickMethods) {
        super(clazz.getProject(), command);

        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mElements = elements;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        mLayoutFileName = layoutFileName;
        mFieldNamePrefix = fieldNamePrefix;
        mCreateHolder = createHolder;
        mSplitOnclickMethods = splitOnclickMethods;
    }

    @Override
    public void run() throws Throwable {


        generateAdapter();

        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }


    /**
     * Create ViewHolder for adapters with injections
     */
    protected void generateAdapter() {
        // view holder class
        StringBuilder holderBuilder = new StringBuilder();
        holderBuilder.append(Utils.getViewHolderClassName());
        holderBuilder.append("(android.view.View view) {");



        for (Element element : mElements) {
            if (!element.used) {
                continue;
            }

            String rPrefix;
            if (element.isAndroidNS) {
                rPrefix = "android.R.id.";
            } else {
                rPrefix = "R.id.";
            }
            holderBuilder.append(element.fieldName);
            holderBuilder.append("=");
            holderBuilder.append("view.findViewById");
            holderBuilder.append('(');
            holderBuilder.append(rPrefix);
            holderBuilder.append(element.id);
            holderBuilder.append(") ");
            holderBuilder.append(";\n");
        }
        holderBuilder.append("}");

        PsiClass viewHolder = mFactory.createClassFromText(holderBuilder.toString(), mClass);
        viewHolder.setName(Utils.getViewHolderClassName());

        for (Element element : mElements) {
            if (!element.used) {
                continue;
            }


            StringBuilder injection = new StringBuilder();
            if (element.nameFull != null && element.nameFull.length() > 0) { // custom package+class
                injection.append(element.nameFull);
            } else if (Definitions.paths.containsKey(element.name)) { // listed class
                injection.append(Definitions.paths.get(element.name));
            } else { // android.widget
                injection.append("android.widget.");
                injection.append(element.name);
            }
            injection.append(" ");
            injection.append(element.fieldName);
            injection.append(";");

            viewHolder.add(mFactory.createFieldFromText(injection.toString(), mClass));
        }

        mClass.add(viewHolder);
        mClass.add(mFactory.createFieldFromText("private "+Utils.getViewHolderClassName()+" m"+Utils.getViewHolderClassName()+";", mClass));

        mClass.addBefore(mFactory.createKeyword("static", mClass), mClass.findInnerClassByName(Utils.getViewHolderClassName(), true));
    }


    private boolean containsButterKnifeInjectLine(PsiMethod method, String line) {
        final PsiCodeBlock body = method.getBody();
        if (body == null) {
            return false;
        }
        PsiStatement[] statements = body.getStatements();
        for (PsiStatement psiStatement : statements) {
            String statementAsString = psiStatement.getText();
            if (psiStatement instanceof PsiExpressionStatement && (statementAsString.contains(line))) {
                return true;
            }
        }
        return false;
    }


}