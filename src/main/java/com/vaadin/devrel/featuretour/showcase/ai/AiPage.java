package com.vaadin.devrel.featuretour.showcase.ai;

import com.vaadin.devrel.featuretour.base.i18n.Messages;
import com.vaadin.devrel.featuretour.base.ui.ShowcasePage;
import com.vaadin.devrel.featuretour.base.ui.Tier;

/**
 * Base class for the AI demos: shows a setup hint when no LLM API key is
 * configured.
 */
abstract class AiPage extends ShowcasePage {

    protected final AiSupport ai;

    protected AiPage(AiSupport ai, String title, String lead, String docsUrl, Tier... tiers) {
        super(title, lead, docsUrl, tiers);
        this.ai = ai;
        if (ai.isConfigured()) {
            addNote(Messages.get("ai.page.powered", ai.modelName(), ai.providerName()));
        } else {
            addNote(Messages.get("ai.page.noLlm")).addClassName("warning");
        }
    }
}
