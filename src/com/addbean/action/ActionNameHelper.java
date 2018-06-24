package com.addbean.action;

import com.addbean.action.injector.InjectWriter;
import com.addbean.model.HttpHelper;
import com.addbean.model.TranslateModel;
import com.addbean.utils.common.Utils;
import com.addbean.utils.http.OnHttpListener;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ActionNameHelper extends BaseGenerateAction {
    @SuppressWarnings("unused")
    public ActionNameHelper() {
        super(null);
    }

    public ActionNameHelper(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        SelectionModel model = editor.getSelectionModel();
        // 选中文字
        String selectedText = model.getSelectedText();
        if (!Utils.checkChinese(selectedText)) {
            Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), MessageType.ERROR, "必须是汉字！");
            return;
        }

        HttpHelper.translate(URLEncoder.encode(selectedText), new OnHttpListener<TranslateModel>() {
            @Override
            public void onSuccess(TranslateModel data) {
                if (data == null || data.getTranslation() == null) {
                    Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), MessageType.ERROR, "生成出错！");
                    return;
                }
                if (data.getTranslation().isEmpty()) {
                    Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), MessageType.WARNING, "没有生成变量名");
                    return;
                }

                List<String> listMenus = new ArrayList<>();
                listMenus.add("变量名");
                listMenus.add("方法名");
                Utils.showListSelecteMenu(e, listMenus, new Utils.OnMenuSelectedListener() {
                    @Override
                    public void onSelected(final int indexMenu, String name, AnActionEvent e) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < data.getTranslation().size(); i++) {
                            if (indexMenu == 0) {
                                list.add(Utils.formatToUCC(data.getTranslation().get(i), true));
                            }
                            if (indexMenu == 1) {
                                list.add(Utils.formatToUCC(data.getTranslation().get(i), false) + "()");
                            }
                        }
                        Utils.showListSelecteMenu(e, list, new Utils.OnMenuSelectedListener() {
                            @Override
                            public void onSelected(int index, String name, AnActionEvent e) {
                                if (indexMenu == 0) {
                                    replaceSelectionText(e, name);
                                }
                                if (indexMenu == 1) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("/**\n");
                                    sb.append("* " + editor.getSelectionModel().getSelectedText());
                                    sb.append("\n");
                                    sb.append("*\n");
                                    sb.append("*/\n");
                                    sb.append("private void ");
                                    sb.append(name);
                                    sb.append("{\n}");
                                    replaceSelectionText(e, sb.toString());
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void onFailure(Exception ex) {
                super.onFailure(ex);
                Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), MessageType.ERROR, "生成出错！");
            }
        });

    }


    /**
     * 替换选中字段；
     *
     * @param event
     * @param
     */
    private void replaceSelectionText(AnActionEvent event, String fReplacement) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                replaceText(editor, fReplacement);
                new ReformatCodeProcessor(event.getProject(), clazz.getContainingFile(), null, false).runWithoutProgress();

            }
        };
        ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(editor.getProject(), runnable));

    }

    protected Runnable getRunnableWrapper(final Project project, final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, runnable, "camelCase", ActionGroup.EMPTY_GROUP);
            }
        };
    }


    public static void replaceText(final Editor editor, final String replacement) {
        new WriteAction<Object>() {
            @Override
            protected void run(Result<Object> result) throws Throwable {
                int start = editor.getSelectionModel().getSelectionStart();
                EditorModificationUtil.insertStringAtCaret(editor, replacement);
                editor.getSelectionModel().setSelection(start, start + replacement.length());
            }
        }.execute().throwException();
    }
}