package com.example.rouminder.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

;

public abstract class BaseGoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<Goal> items;
    protected GoalManager goalManager;
    protected GoalManager.Domain domain;
    protected Comparator<Goal> comparator;
    protected GoalManager.OnGoalChangeListener listener;

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
    }

    public void setComparator(Comparator<Goal> comparator) {
        this.comparator = comparator;
    }

    private void setListener() {
        if(listener != null)
            listener.remove();
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
                items = goalManager.getGoals(LocalDateTime.now(), domain, GoalManager.Status.ALL);
            }
        };
        goalManager.setOnGoalChangeListener(listener);
    }

    private void setDataset() {
        notifyItemRangeRemoved(0, items.size());
        items = goalManager.getGoals(LocalDateTime.now(), domain, GoalManager.Status.ALL).stream().sorted(comparator).collect(Collectors.toList());
        notifyItemRangeInserted(0, items.size());
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
        }
    }

    private void removeItem(int id) {
        int position = getItemPosition(id);
        if (position != -1) {
            Goal goal = goalManager.getGoal(id);
            items.remove(goal);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
