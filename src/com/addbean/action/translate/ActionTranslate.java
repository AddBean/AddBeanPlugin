package com.addbean.action.translate;

import com.addbean.model.HttpHelper;
import com.addbean.model.TranslateModel;
import com.addbean.utils.common.Utils;
import com.addbean.utils.http.OnHttpListener;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.MessageType;

import java.net.URLEncoder;

public class ActionTranslate extends BaseGenerateAction {
    @SuppressWarnings("unused")
    public ActionTranslate() {
        super(null);
    }

    public ActionTranslate(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        SelectionModel model = editor.getSelectionModel();
        // 选中文字
        String selectedText = model.getSelectedText();
//        Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR),  MessageType.INFO,"正在查询："+ selectedText);
        HttpHelper.translate(URLEncoder.encode(selectedText), new OnHttpListener<TranslateModel>() {
            @Override
            public void onSuccess(TranslateModel data) {
                String msg = "";
                MessageType msgType;
                try {
                    StringBuilder result = new StringBuilder();
                    result.append("\"" + data.getQuery() + "\"\n\n");
                    result.append("释义：" + data.getTranslation() + "\n");
                    if (data.getBasic()!=null&&data.getBasic().getExplains() != null) {
                        for (int i = 0; i < data.getBasic().getExplains().size(); i++) {
                            result.append("   " + data.getBasic().getExplains().get(i) + "\n");
                        }
                    }
                    if (data.getWeb() != null) {
                        result.append("\n更多：\n");
                        for (int i = 0; i < data.getWeb().size(); i++) {
                            result.append("   " + data.getWeb().get(i).getKey() + "：" + data.getWeb().get(i).getValue() + "\n");
                        }
                    }
                    msg = result.toString();
                    msgType = MessageType.INFO;
                } catch (Exception e) {
                    msgType = MessageType.ERROR;
                    msg = e.getMessage();
                }

                Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), msgType, msg);
            }

            @Override
            public void onFailure(Exception ex) {
                super.onFailure(ex);
                Utils.showPopupWindow(e.getData(PlatformDataKeys.EDITOR), MessageType.ERROR, ex.getMessage());
            }
        });

    }
}
