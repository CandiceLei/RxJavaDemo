package yt.com.rxjavademo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton btnBasic;
    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }

    private void initView() {
        btnBasic = (AppCompatButton) findViewById(R.id.btn_basic);
        btnBasic.setOnClickListener(this);
        AppCompatButton btnJust = (AppCompatButton) findViewById(R.id.btn_just);
        btnJust.setOnClickListener(this);
        AppCompatButton btnFrom = (AppCompatButton) findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(this);
        AppCompatButton btnMap = (AppCompatButton) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(this);
        AppCompatButton btnFlatMap = (AppCompatButton) findViewById(R.id.btn_flatmap);
        btnFlatMap.setOnClickListener(this);
        ((AppCompatButton) findViewById(R.id.btn_zip)).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_basic:
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("李白");
                        e.onNext("杜甫");
                        e.onNext("李贺");
                        e.onComplete();
//                        e.onError(new Throwable("测试"));
//                        e.onNext("白居易");
//                        e.onNext("素食");
//                        e.onError(new Throwable("测试"));

                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread(), false, 100)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.e(TAG, "onSubscribe");
                            }

                            @Override
                            public void onNext(String value) {
                                Log.e(TAG, "onNext:" + value);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError:" + e.getMessage());

                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete:");

                            }
                        });
                break;

            case R.id.btn_just:
                Observable.just("杨颖峰", "月眉儿")
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.e(TAG, "just-----accept:" + s);
                            }
                        });
                break;
            case R.id.btn_from:
                String[] datas = {"杨颖峰", "月眉儿"};
                Observable.fromArray(datas)
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.e(TAG, "from-----accept:" + s);
                            }
                        });
                break;
            case R.id.btn_map:
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                        e.onNext(4);
                        e.onComplete();

                    }
                }).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "this is integer:" + integer;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "map: " + s);
                    }
                });
            case R.id.btn_flatmap:
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                        e.onNext(4);
                        e.onComplete();
                    }
                }).concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            list.add("this integer:" + integer);
                        }
                        return Observable.fromIterable(list).delay(3, TimeUnit.MICROSECONDS);
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "flatmap: " + s);
                    }
                });
                break;
            case R.id.btn_zip:
//                testZip();
                testZip1();
                break;
            default:
                break;
        }
    }

    private void testZip1() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        SystemClock.sleep(2000);
                        Log.e(TAG, "integer:" + integer);
                    }
                });
    }

    private void testZip() {
        Observable.zip(Observable.create(new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                        for (int i = 0; ; i++) {
                            e.onNext(i);
                        }

//                        e.onNext(1);
//                        e.onNext(2);
//                        e.onNext(3);
//                        e.onNext(4);
//                        e.onNext(5);
//                        e.onNext(6);
//                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                , Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("test1");
                        e.onNext("test2");
                        e.onNext("test3");
                        e.onNext("test4");
//                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                , new BiFunction<Integer, String, String>() {
                    @Override
                    public String apply(Integer integer, String s) throws Exception {

                        return "this is " + s + "--" + integer;
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "zip:" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "throwable:" + throwable.getMessage());
            }
        });
    }

}
