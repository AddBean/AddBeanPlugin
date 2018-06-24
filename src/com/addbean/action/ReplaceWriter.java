package com.addbean.action;

import com.addbean.utils.common.Utils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class ReplaceWriter extends WriteCommandAction.Simple {

    private final PsiClass mClass;
    private final Project mProject;
    private final PsiFile mFile;
    private final PsiElementFactory mFactory;

    public ReplaceWriter(PsiFile file, PsiClass clazz, String command) {
        super(clazz.getProject(), command);
        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
    }


    @Override
    protected void run() throws Throwable {
        JavaPsiFacade.getElementFactory(mProject);
        PsiCodeBlock block=mFactory.createCodeBlock();
//        block.
    }
}
