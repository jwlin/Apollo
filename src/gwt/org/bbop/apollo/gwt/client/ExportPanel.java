package org.bbop.apollo.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.bbop.apollo.gwt.client.dto.OrganismInfo;
import org.bbop.apollo.gwt.client.dto.SequenceInfo;

import java.util.List;

/**
 * Created by ndunn on 1/27/15.
 */
public class ExportPanel extends DialogBox{
    private String type;

    private OrganismInfo organismInfo ;
    private List<SequenceInfo> sequenceList ;

    interface ExportPanelUiBinder extends UiBinder<Widget, ExportPanel> {
    }

    private static ExportPanelUiBinder ourUiBinder = GWT.create(ExportPanelUiBinder.class);
    @UiField
    HTML organismLabel;
    @UiField
    HTML sequenceInfoLabel;
    @UiField
    HTML typeLabel;
    @UiField
    Button exportButton;

    public ExportPanel() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setText("Export");
        setGlassEnabled(true);
        center();
//        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setOrganismInfo(OrganismInfo organismInfo) {
        this.organismInfo = organismInfo;
        organismLabel.setHTML(organismInfo.getName());
    }

    public void setSequenceList(List<SequenceInfo> sequenceList) {
        this.sequenceList = sequenceList;
        sequenceInfoLabel.setHTML(this.sequenceList.size() + " exported ");
    }

    public void setType(String type) {
        this.type = type;
        typeLabel.setHTML("Type: "+this.type);
    }

    public String getType() {
        return type;
    }

    @UiHandler("exportButton")
    public void doExport(ClickEvent clickEvent){
        Window.alert("doing export with REST IO/Service ");
        hide();
    }

}