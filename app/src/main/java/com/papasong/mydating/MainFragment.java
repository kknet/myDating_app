package com.papasong.mydating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.papasong.mydating.adapter.GalleryListAdapter;
import com.papasong.mydating.adapter.ProfilesSpotlightListAdapter;
import com.papasong.mydating.app.App;
import com.papasong.mydating.constants.Constants;
import com.papasong.mydating.model.Image;
import com.papasong.mydating.model.Profile;
import com.papasong.mydating.util.CustomRequest;
import com.papasong.mydating.util.Helper;
import com.papasong.mydating.view.SpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";
    private static final String STATE_LIST_2 = "State Adapter Data 2";
    private static final String STATE_LIST_3 = "State Adapter Data 3";

    TextView mMessage;
    ImageView mSplash;

    CardView mSpotlightCard, mFeedCard;
    Button mSpotlightMoreButton;

    RecyclerView mRecyclerView, mSpotlightRecyclerView;
    NestedScrollView mNestedView;

    SwipeRefreshLayout mItemsContainer;

    private TextView mModePanelTitle;
    private SwitchCompat mModeSwitch;

    private ArrayList<Profile> spotlightList;
    private ProfilesSpotlightListAdapter spotlightAdapter;

    private ArrayList<Image> itemsList;
    private GalleryListAdapter itemsAdapter;

    long itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    private Double lat = 39.9199, lng = 32.8543; // Ankara

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Intent i = getActivity().getIntent();

        //itemId = i.getLongExtra("itemId", 0);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new GalleryListAdapter(getActivity(), itemsList);

            spotlightList = savedInstanceState.getParcelableArrayList(STATE_LIST_2);
            spotlightAdapter = new ProfilesSpotlightListAdapter(getActivity(), spotlightList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getLong("itemId");

        } else {

            itemsList = new ArrayList<Image>();
            itemsAdapter = new GalleryListAdapter(getActivity(), itemsList);

            spotlightList = new ArrayList<Profile>();
            spotlightAdapter = new ProfilesSpotlightListAdapter(getActivity(), spotlightList);

            restore = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle(R.string.app_name);

        mItemsContainer = rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mSpotlightCard = rootView.findViewById(R.id.spotlight_card);
        mFeedCard = rootView.findViewById(R.id.media_feed_card);

        mSpotlightCard.setVisibility(View.GONE);
        mFeedCard.setVisibility(View.GONE);

        mSpotlightMoreButton = rootView.findViewById(R.id.spotlight_card_button);

        mSpotlightRecyclerView = rootView.findViewById(R.id.spotlight_recycler_view);

        //

        mSpotlightRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mSpotlightRecyclerView.setAdapter(spotlightAdapter);

        spotlightAdapter.setOnItemClickListener(new ProfilesSpotlightListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Profile obj, int position) {

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("profileId", obj.getId());
                startActivity(intent);
            }
        });

        mSpotlightMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SpotlightActivity.class);
                startActivity(intent);
            }
        });

        //

        mMessage = rootView.findViewById(R.id.message);
        mSplash = rootView.findViewById(R.id.splash);

        mNestedView = rootView.findViewById(R.id.nested_view);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        int columns = 3;

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(columns, Helper.dpToPx(getActivity(), 4), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.addOnItemTouchListener(new GalleryListAdapter.RecyclerTouchListener(getActivity(), mRecyclerView, new GalleryListAdapter.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                Image img = itemsList.get(position);

                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra("itemId", img.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRecyclerView.setNestedScrollingEnabled(false);

        mNestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY < oldScrollY) { // up

                }

                if (scrollY > oldScrollY) { // down

                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                    if (!loadingMore && (viewMore) && !(mItemsContainer.isRefreshing())) {

                        mItemsContainer.setRefreshing(true);

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        // Mode panel

        mModePanelTitle = (TextView) rootView.findViewById(R.id.mode_switch_panel_title);
        mModeSwitch = (SwitchCompat) rootView.findViewById(R.id.mode_switch);

        mModeSwitch.setOnCheckedChangeListener(null);

        if (App.getInstance().getFeedMode() == 1) {

            mModeSwitch.setChecked(true);
            mModePanelTitle.setText(R.string.label_feed_mode_1);

        } else {

            mModeSwitch.setChecked(false);
            mModePanelTitle.setText(R.string.label_feed_mode_0);
        }

        mModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    App.getInstance().setFeedMode(1);
                    mModePanelTitle.setText(R.string.label_feed_mode_1);

                } else {

                    App.getInstance().setFeedMode(0);
                    mModePanelTitle.setText(R.string.label_feed_mode_0);
                }

                App.getInstance().saveData();

                itemId = 0;
                getItems();
            }
        });

        //

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();

        } else {

            updateView();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateView() {

        hideMessage();

        mSpotlightCard.setVisibility(View.VISIBLE);
        mFeedCard.setVisibility(View.VISIBLE);

        if (spotlightAdapter.getItemCount() == 0) {

            mSpotlightRecyclerView.setVisibility(View.GONE);

        } else {

            mSpotlightRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;

            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putLong("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
        outState.putParcelableArrayList(STATE_LIST_2, spotlightList);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);
        mModeSwitch.setEnabled(false);

        String url = METHOD_GALLERY_FEED;

        if (App.getInstance().getFeedMode() != 1) {

            url = METHOD_GALLERY_STREAM;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MainFragment Not Added to Activity");

                            return;
                        }

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Image item = new Image(itemObj);

                                            itemsList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            if (spotlightAdapter.getItemCount() == 0) {

                                getSpotlightItems();
                            }

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MainFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(itemId));
                params.put("language", App.getInstance().getLanguage());

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getSpotlightItems() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_SPOTLIGHT_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "MainFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                if (response.has("items")) {

                                    JSONArray usersArray = response.getJSONArray("items");

                                    arrayLength = usersArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(userObj);

                                            spotlightList.add(profile);
                                        }
                                    }
                                }

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            spotlightAdapter.notifyDataSetChanged();

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "MainFragment Not Added to Activity");

                    return;
                }

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("itemId", Long.toString(0));
                params.put("language", "en");

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        mModeSwitch.setEnabled(true);

        viewMore = arrayLength == LIST_ITEMS;

        hideMessage();

        itemsAdapter.notifyDataSetChanged();
        spotlightAdapter.notifyDataSetChanged();

        mSpotlightCard.setVisibility(View.VISIBLE);
        mFeedCard.setVisibility(View.VISIBLE);

        if (spotlightAdapter.getItemCount() == 0) {

            mSpotlightRecyclerView.setVisibility(View.GONE);

        } else {

            mSpotlightRecyclerView.setVisibility(View.VISIBLE);
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);

        mSpotlightCard.setVisibility(View.GONE);
        mFeedCard.setVisibility(View.GONE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_filters);
        item.setVisible(false);

        item = menu.findItem(R.id.action_remove_all);
        item.setVisible(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}