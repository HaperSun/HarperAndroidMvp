package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityExpandableTextBinding;
import com.sun.emoji.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note: 可展开的TextView实例
 */
public class ExpandableTextActivity extends BaseMvpActivity {

    private List<String> beans;
    private ActivityExpandableTextBinding bind;

    public static void start(Context context) {
        Intent intent = new Intent(context, ExpandableTextActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_expandable_text;
    }

    @Override
    public void initView() {
        bind = (ActivityExpandableTextBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        beans = new ArrayList<>();
        beans.add("大家肯定都看过琼瑶剧吧，琼瑶的作品一直都有很独特的文风，清新脱俗的爱情故事，使得她的众多作品都深受大家喜欢，在中国电视剧行业自成一体。能够被选中琼瑶剧的女主，在颜值方面肯定是过关的，虽然琼瑶的三观不行，但是看美女的眼光一直都很高，普遍颜值在娱乐圈里都是比较出众的。");
        beans.add("大家肯定都看过琼瑶剧吧，琼瑶的作品一直都有很独特的文风，清新脱俗的爱情故事，使得她的众多作品都深受大家喜欢，在中国电视剧行业自成一体。能够被选中琼瑶剧的女主，在颜值方面肯定是过关的，虽然琼瑶的三观不行，但是看美女的眼光一直都很高，普遍颜值在娱乐圈里都是比较出众的。");
        beans.add("不难发现，在琼瑶剧里，大部分的女主人都有很清透的眼睛，大眼睛能够让你在电视剧中看起来更有神态感，可以从整体上优化长相。大眼睛最大的好处就是能够让整一个脸部看起来更加的美感，可以协调五官增加颜值，表演起来会更加的生动，更能够带入到剧情中。");
        beans.add("小眼睛不一定不是美女，但大眼睛绝对算得上是美女。随着时代的发展，各种医疗技术在增加，这时候单眼皮的女生也拥有了双眼皮，但是还是会不够的自然。在那个时代，没有过多的虚假成分，琼瑶剧中的女主角都是纯天然美女，简约的淡妆即可把形象凸显出来，五官非常的立体。");
        beans.add("小眼睛不一定不是美女，但大眼睛绝对算得上是美女。随着时代的发展，各种医疗技术在增加，这时候单眼皮的女生也拥有了双眼皮，但是还是会不够的自然。在那个时代，没有过多的虚假成分，琼瑶剧中的女主角都是纯天然美女，简约的淡妆即可把形象凸显出来，五官非常的立体。");
        beans.add("琼瑶剧中很多的女主，她们虽然一眼看去不算特别的惊艳，但是脸部轮廓非常有特性和识别性，总能在别人的心目中留下深刻的印象。就拿黄奕来说，其实黄奕在娱乐圈里面算不上特别的精致，但是她的脸部非常的特点，饱满的脸颊，使得它在古装剧中，更能够凸显那种楚楚可怜感。");
        beans.add("现在大家一直都认为瓜子脸就很好看，实际上在那个年代真正的美人，不一定需要有这些特征，反倒是能够让人一眼就记住的人，才是真正的美。琼瑶剧中大部分的女主角，脸颊两边会过高一些，眼神是很深邃的，整体给人一种很温婉素雅的感觉，即使不上妆，容貌方面也很突出");
        beans.add("现在大家一直都认为瓜子脸就很好看，实际上在那个年代真正的美人，不一定需要有这些特征，反倒是能够让人一眼就记住的人，才是真正的美。琼瑶剧中大部分的女主角，脸颊两边会过高一些，眼神是很深邃的，整体给人一种很温婉素雅的感觉，即使不上妆，容貌方面也很突出");
        beans.add("黄奕在参加综艺节目时，就曾透露说，琼瑶选演员的时候，对于哭戏是比较注重的，哭戏好看的话那么会更加的入戏，也才能进入琼瑶的眼。在琼瑶阿姨看来，会演戏的人就必须拥有哭感，双眼中必须能够包含泪水的，既要让泪水饱含在眼睛中，又不能流在脸部，整一个过程可以达到楚楚可怜的状态。");
        beans.add("黄奕在参加综艺节目时，就曾透露说，琼瑶选演员的时候，对于哭戏是比较注重的，哭戏好看的话那么会更加的入戏，也才能进入琼瑶的眼。在琼瑶阿姨看来，会演戏的人就必须拥有哭感，双眼中必须能够包含泪水的，既要让泪水饱含在眼睛中，又不能流在脸部，整一个过程可以达到楚楚可怜的状态。");
        Adapter adapter = new Adapter();
        bind.recyclerView.setAdapter(adapter);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expandable_text, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.textView.setText(beans.get(position));
        }

        @Override
        public int getItemCount() {
            return beans.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            ExpandableTextView textView;

            public Holder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.expandable_text);
            }
        }
    }
}