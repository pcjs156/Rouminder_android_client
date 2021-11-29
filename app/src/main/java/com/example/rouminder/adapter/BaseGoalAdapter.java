package com.example.rouminder.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.fragments.GoalFragment;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

;

public abstract class BaseGoalAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected List<Goal> items;
    protected GoalManager goalManager;
    protected GoalManager.Domain domain;
    protected Comparator<Goal> comparator;
    protected GoalManager.OnGoalChangeListener listener;

    protected double progress;
    protected int complete;
    protected int inComplete;

    public BaseGoalAdapter(GoalManager goalManager, GoalManager.Domain domain, Comparator<Goal> comparator) {
        super();
        this.goalManager = goalManager;
        this.domain = domain;
        this.comparator = comparator;
        this.items = goalManager.getGoals(LocalDateTime.now(), domain, GoalManager.Status.ALL).stream().sorted(comparator).collect(Collectors.toList());
        setListener();
    }

    public void setDomain(GoalManager.Domain domain) {
        this.domain = domain;
        setListener();
        setDataset();

        calculateProgress();
        GoalFragment.me.setProgress();
    }

    public void setComparator(Comparator<Goal> comparator) {
        this.comparator = comparator;
    }

    private void setListener() {
        if(listener != null)
            goalManager.removeOnGoalChangeListener(listener);
        listener = goalManager.new OnGoalWithCriteriaChangeListener(domain, GoalManager.Status.ALL) {
            @Override
            public void onGoalWithCriteriaAdd(int id) {
                addItem(id);
            }

            @Override
            public void onGoalWithCriteriaUpdate(int id) {
                updateItem(id);
            }

            @Override
            public void onGoalWithCriteriaRemove(int id) {
                removeItem(id);
            }

            @Override
            public void onDomainChanged() {
                setDataset();
            }
        };
        goalManager.setOnGoalChangeListener(listener);
    }

    private void setDataset() {
        items = goalManager.getGoals(LocalDateTime.now(), domain, GoalManager.Status.ALL).stream().sorted(comparator).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    private int getItemPosition(int id) {
        Goal goal = items.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
        return (goal == null) ? -1 : items.indexOf(goal);
    }

    private void addItem(int id) {
        int position = getItemPosition(id);
        if (position == -1) {
            Goal goal = goalManager.getGoal(id);
            items.add(goal);
            items.sort(comparator);
            position = getItemPosition(id);
            notifyItemInserted(position);

            calculateProgress();
            GoalFragment.me.setProgress();
        }
    }

    private void updateItem(int id) {
        int position = getItemPosition(id);
        if (position != -1) {
            items.sort(comparator);
            int newPosition = getItemPosition(id);
            if(position == newPosition) {
                notifyItemChanged(position);
            } else {
                notifyItemMoved(position, newPosition);
            }

            calculateProgress();
            GoalFragment.me.setProgress();
        }
    }

    private void removeItem(int id) {
        int position = getItemPosition(id);
        if (position != -1) {
            Goal goal = goalManager.getGoal(id);
            items.remove(goal);
            notifyItemRemoved(position);

            calculateProgress();
            GoalFragment.me.setProgress();
        }
    }

    public void calculateProgress() {
        complete = 0;
        inComplete = 0;

        if (items.size() == 0) {
            progress = 0;
            return;
        }

        items.forEach(item->{
            if (item.isAccomplished()) {
                complete++;
            } else {
                inComplete++;
            }
        });

        progress = 100 * complete / (complete + inComplete);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
