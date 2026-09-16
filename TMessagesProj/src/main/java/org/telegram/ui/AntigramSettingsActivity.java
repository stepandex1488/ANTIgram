/*
 * ANTIgram Settings Activity
 */

package org.telegram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AntigramConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class AntigramSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;
    private ArrayList<ItemInner> items = new ArrayList<>();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CHECK = 1;
    private static final int VIEW_TYPE_TEXT = 2;
    private static final int VIEW_TYPE_SHADOW = 3;

    private static final int ITEM_BYPASS_RESTRICTIONS = 1;
    private static final int ITEM_ALLOW_SCREENSHOTS = 2;
    private static final int ITEM_AI_ENABLED = 3;
    private static final int ITEM_AI_KEY = 4;
    private static final int ITEM_SPAM_FILTER = 5;
    private static final int ITEM_SPAM_KEYWORDS = 6;
    private static final int ITEM_VERSION = 7;

    private static class ItemInner {
        int id;
        int viewType;
        CharSequence text;
        CharSequence subtext;
        CharSequence value;

        ItemInner(int id, int viewType, CharSequence text, CharSequence subtext, CharSequence value) {
            this.id = id;
            this.viewType = viewType;
            this.text = text;
            this.subtext = subtext;
            this.value = value;
        }
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("ANTIgram");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter());

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setDurations(300);
        itemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        listView.setItemAnimator(itemAnimator);

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position < 0 || position >= items.size()) {
                return;
            }
            ItemInner item = items.get(position);
            if (item.id == ITEM_BYPASS_RESTRICTIONS) {
                AntigramConfig.setBypassRestrictedContent(!AntigramConfig.bypassRestrictedContent);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(AntigramConfig.bypassRestrictedContent);
                }
            } else if (item.id == ITEM_ALLOW_SCREENSHOTS) {
                AntigramConfig.setAllowScreenshots(!AntigramConfig.allowScreenshots);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(AntigramConfig.allowScreenshots);
                }
            } else if (item.id == ITEM_AI_ENABLED) {
                AntigramConfig.setAiEnabled(!AntigramConfig.aiEnabled);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(AntigramConfig.aiEnabled);
                }
            } else if (item.id == ITEM_AI_KEY) {
                showApiKeyDialog(context);
            } else if (item.id == ITEM_SPAM_FILTER) {
                AntigramConfig.setSpamFilterEnabled(!AntigramConfig.spamFilterEnabled);
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(AntigramConfig.spamFilterEnabled);
                }
            } else if (item.id == ITEM_SPAM_KEYWORDS) {
                showKeywordsDialog(context);
            } else if (item.id == ITEM_VERSION) {
                Toast.makeText(context, "ANTIgram v1.0.0", Toast.LENGTH_SHORT).show();
            }
        });

        updateItems();
        return fragmentView;
    }

    private void showApiKeyDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Google Gemini API Key");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setText(AntigramConfig.aiApiKey);
        input.setHint("AIzaSy...");

        FrameLayout container = new FrameLayout(context);
        container.setPadding(dp(20), dp(10), dp(20), dp(10));
        container.addView(input, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        builder.setView(container);

        builder.setPositiveButton("OK", (dialog, which) -> {
            AntigramConfig.setAiApiKey(input.getText().toString().trim());
            updateItems();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showKeywordsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Стоп-слова для фильтра спама");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(AntigramConfig.spamFilterKeywords);
        input.setHint("слово1, слово2, слово3");

        FrameLayout container = new FrameLayout(context);
        container.setPadding(dp(20), dp(10), dp(20), dp(10));
        container.addView(input, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        builder.setView(container);

        builder.setPositiveButton("OK", (dialog, which) -> {
            AntigramConfig.setSpamFilterKeywords(input.getText().toString().trim());
            updateItems();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void updateItems() {
        items.clear();

        // 1. Ограничения контента
        items.add(new ItemInner(0, VIEW_TYPE_HEADER, "Обход ограничений", null, null));
        items.add(new ItemInner(ITEM_BYPASS_RESTRICTIONS, VIEW_TYPE_CHECK, "Снять защиту контента", "Разрешить пересылку, копирование и скачивание из защищенных каналов", null));
        items.add(new ItemInner(ITEM_ALLOW_SCREENSHOTS, VIEW_TYPE_CHECK, "Разрешить скриншоты", "Отключение запрета на создание скриншотов в секретных и закрытых чатах", null));
        items.add(new ItemInner(0, VIEW_TYPE_SHADOW, null, null, null));

        // 2. AI модуль
        items.add(new ItemInner(0, VIEW_TYPE_HEADER, "AI-помощник (Gemini)", null, null));
        items.add(new ItemInner(ITEM_AI_ENABLED, VIEW_TYPE_CHECK, "Включить AI функции", "Суммаризация чатов и генерация умных ответов", null));
        String keyMask = TextUtils.isEmpty(AntigramConfig.aiApiKey) ? "Не настроен" : "••••••••" + (AntigramConfig.aiApiKey.length() > 4 ? AntigramConfig.aiApiKey.substring(AntigramConfig.aiApiKey.length() - 4) : "");
        items.add(new ItemInner(ITEM_AI_KEY, VIEW_TYPE_TEXT, "API Ключ Gemini", null, keyMask));
        items.add(new ItemInner(0, VIEW_TYPE_SHADOW, null, null, null));

        // 3. Фильтрация спама
        items.add(new ItemInner(0, VIEW_TYPE_HEADER, "Безопасность и спам", null, null));
        items.add(new ItemInner(ITEM_SPAM_FILTER, VIEW_TYPE_CHECK, "Фильтрация спама", "Скрывать сообщения, содержащие подозрительные слова", null));
        items.add(new ItemInner(ITEM_SPAM_KEYWORDS, VIEW_TYPE_TEXT, "Список стоп-слов", null, "Настроить"));
        items.add(new ItemInner(0, VIEW_TYPE_SHADOW, null, null, null));

        // 4. О клиенте
        items.add(new ItemInner(0, VIEW_TYPE_HEADER, "О клиенте", null, null));
        items.add(new ItemInner(ITEM_VERSION, VIEW_TYPE_TEXT, "ANTIgram", null, "v1.0.0 (Android)"));
        items.add(new ItemInner(0, VIEW_TYPE_SHADOW, null, null, null));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    view = new HeaderCell(parent.getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_CHECK:
                    view = new TextCheckCell(parent.getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_TEXT:
                    view = new TextCell(parent.getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_SHADOW:
                default:
                    view = new ShadowSectionCell(parent.getContext());
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (position < 0 || position >= items.size()) {
                return;
            }
            ItemInner item = items.get(position);
            int viewType = holder.getItemViewType();
            boolean hasDivider = position + 1 < items.size() && items.get(position + 1).viewType != VIEW_TYPE_SHADOW && items.get(position + 1).viewType != VIEW_TYPE_HEADER;

            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    ((HeaderCell) holder.itemView).setText(item.text);
                    break;
                case VIEW_TYPE_CHECK: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    boolean checked = false;
                    if (item.id == ITEM_BYPASS_RESTRICTIONS) {
                        checked = AntigramConfig.bypassRestrictedContent;
                    } else if (item.id == ITEM_ALLOW_SCREENSHOTS) {
                        checked = AntigramConfig.allowScreenshots;
                    } else if (item.id == ITEM_AI_ENABLED) {
                        checked = AntigramConfig.aiEnabled;
                    } else if (item.id == ITEM_SPAM_FILTER) {
                        checked = AntigramConfig.spamFilterEnabled;
                    }
                    if (item.subtext != null) {
                        cell.setTextAndValueAndCheck(item.text.toString(), item.subtext.toString(), checked, true, hasDivider);
                    } else {
                        cell.setTextAndCheck(item.text, checked, hasDivider);
                    }
                    break;
                }
                case VIEW_TYPE_TEXT: {
                    TextCell cell = (TextCell) holder.itemView;
                    cell.setTextAndValue(item.text, item.value, hasDivider);
                    break;
                }
                case VIEW_TYPE_SHADOW:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType == VIEW_TYPE_CHECK || viewType == VIEW_TYPE_TEXT;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 0 || position >= items.size()) {
                return VIEW_TYPE_SHADOW;
            }
            return items.get(position).viewType;
        }
    }
}
