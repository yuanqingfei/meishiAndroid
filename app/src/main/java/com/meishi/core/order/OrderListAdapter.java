package com.meishi.core.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.meishi.R;
import com.meishi.model.Order;
import com.meishi.model.OrderStatusEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Aaron on 2015/6/10.
 */
public class OrderListAdapter extends ArrayAdapter<Order> {

    private Context context;
    private List<Order> orders;
    private int layoutResourceId;

    public OrderListAdapter(Context context, int layoutResourceId, List<Order> orders) {
        super(context, layoutResourceId, orders);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.orders = orders;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        OrderHolder holder = null;

        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, parent, false);
            holder = new OrderHolder();
            holder.orderId = (TextView) rowView.findViewById(R.id.row_order_id);
            holder.orderDate = (TextView) rowView.findViewById(R.id.row_order_date);
            holder.orderPrice = (TextView) rowView.findViewById(R.id.row_order_price);
            holder.statusButton = (Button) rowView.findViewById(R.id.row_order_status);
            rowView.setTag(holder);
        } else {
            holder = (OrderHolder) rowView.getTag();
        }

        Order order = orders.get(position);
        holder.orderId.setText(order.getId());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String dateString = dateFormat.format(order.getOrderTime());
        holder.orderDate.setText(dateString);

        holder.orderPrice.setText(order.getTotalPrice() != null ? order.getTotalPrice().toString() : "0");

        List<OrderStatusEntry> statuses = order.getStatuses();
        final String[] statusString = new String[statuses.size()];
        for (int i = 0; i < statuses.size(); i++) {
            OrderStatusEntry statusEntry = statuses.get(i);
            statusString[i] = statusEntry.getStatus().name() + "  " + dateFormat.format(statusEntry.getTime());
        }
        holder.statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("订单状态").setItems(statusString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        }
                    }
                }).show();
            }
        });
        return rowView;
    }

    static class OrderHolder {
        TextView orderId;
        TextView orderDate;
        TextView orderPrice;
        Button statusButton;
    }
}
