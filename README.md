
# Android-PickerView

## Introduction

This is a library for android to pick date or options like IOS system WheelView widget.
Forked from https://github.com/Bigkoo/Android-PickerView, which seems abandoned. The idea is to fix bugs and review/merge all interesting pull requests.
Will try to migrate it to Kotlin as well.
Feel free to collaborate!


# How to use

### 1. Add the dependencies

It's on jitpack, so you'll need to add this to your root ```build.gradle```:
```
maven { url 'https://jitpack.io' }
```

Next, add the librarby in your ```app/build.gradle``` file:
```java
implementation 'com.amanzan:Android-PickerView:4.2.0'
```

### 2. Add the following code in your Activity：

```java
//TimePicker
 pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
@Override
public void onTimeSelect(Date date,View v) {//Callback
        tvTime.setText(getTime(date));
        }
        })
        .build();
        pvTime.show();
```

```java
//OptionsPicker
 pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
@Override
public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
        ////Callback
        String tx = options1Items.get(options1).getPickerViewText()
        + options2Items.get(options1).get(option2)
        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
        tvOptions.setText(tx);
        }
        }).build();

        //pvOptions.setPicker(options1Items);
        pvOptions.setPicker(options1Items, options2Items);
        //pvOptions.setPicker(options1Items, options2Items, options3Items);
        pvOptions.show();
```
## Just so easy ~


### Notes（2017-7-10）
- when we start setting the date, we need to pay special attention.

- reason: the internal component of the Calendar adds 1 processing, which made the month's number of  count reduced one.
- error usage case:

>StartDate.set (2013,1,1);

>EndDate.set (2020,12,31);

- correct use case:

>StartDate.set (2013,0,1);

>EndDate.set (2020,11,31);
</br>


If the default style does not meet your expectations, You can also customize attributes to apply

## Customize Useage：
```java
 Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013,0,1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020,11,1);

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
@Override
public void onTimeSelect(Date date,View v) {//callback
        tvTime.setText(getTime(date));
        }
        })
        .setType(new boolean[]{false, false, false, true, true, false})// type of date 
        .setCancelText("Cancel")
        .setSubmitText("Sure")
        .setContentSize(18)
        .setTitleSize(20)
        .setTitleText("Title")
        .setOutSideCancelable(false)// default is true
        .isCyclic(true)// default is false
        .setTitleColor(Color.BLACK)
        .setSubmitColor(Color.BLUE)
        .setCancelColor(Color.BLUE)
        .setTitleBgColor(0xFF666666)//night mode
        .setBgColor(0xFF333333)//night mode
        .setRangDate(startDate,endDate)
        .setLabel("year","month","day","hours","mins","seconds")
        .build();
```

```java
pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
@Override
public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
        ////Callback
        String tx = options1Items.get(options1).getPickerViewText()
        + options2Items.get(options1).get(option2)
        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
        tvOptions.setText(tx);
        }
        })
        .setSubmitText("sure")
        .setCancelText("cancel")
        .setTitleText("title")
        .setSubCalSize(18)
        .setTitleSize(20)
        .setTitleColor(Color.BLACK)
        .setSubmitColor(Color.BLUE)
        .setCancelColor(Color.BLUE)
        .setTitleBgColor(0xFF666666)//night mode
        .setBgColor(0xFF444444)//night mode
        .setContentTextSize(18)
        .setLinkage(false)
        .isCenterLabel(false) //default is true , if you choose false , the label text will add to all item ContentText right
        .setLabels("province", "city", "district")
        .setCyclic(false, false, false)
        .setSelectOptions(0, 0, 0)  //default options
        .setOutSideCancelable(false)//dismiss， default is true
        .isRestoreItem(true)// restore option with first item when select changed。
        .build();

        pvOptions.setPicker(options1Items, options2Items, options3Items);
```
## Customize Layout：
```java
  private void initCustomTimePicker() {
        // be careful：In the custom layout， the layout of the ID for optionspicker
        // or TimePicker and its child widget must not be modified,
        // otherwise  will be reported NullPointerException
        // For more details， Please refer to the two custom layouts in demo

        Calendar selectedDate = Calendar.getInstance();//System current time
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013,1,23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2019,2,28);

        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
@Override
public void onTimeSelect(Date date, View v) {//call back
        btn_CustomTime.setText(getTime(date));
        }
        })       .setType(new boolean[]{true, true, true, false, false, false})// year - month - day
        .setDate(selectedDate)
        .setRangDate(startDate,endDate)
        .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

@Override
public void customLayout(View v) {
final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        pvCustomTime.returnData(tvSubmit);
        }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        pvCustomTime.dismiss();
        }
        });
        }
        })
        .setDividerColor(Color.BLACK)
        .build();

        }
```

## If you need to set the non-linkage data：

```java
pvNoLinkOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {

@Override
public void onOptionsSelect(int options1, int options2, int options3, View v) {

        String str = "food:"+food.get(options1)
        +"\nclothes:"+clothes.get(options2)
        +"\ncomputer:"+computer.get(options3);

        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
        }
        }).build();
        pvNoLinkOptions.setNPicker(food,clothes,computer);
        pvNoLinkOptions.show();
```

## For more detail, please refer to the Demo code, If there is still doubt about you, please [New Issue](https://github.com/Bigkoo/Android-PickerView/issues)

###  [Here is demo code](https://github.com/Bigkoo/Android-PickerView/blob/master/app/src/main/java/com/bigkoo/pickerviewdemo/MainActivity.java)

###  [Methods-and-parameters](https://github.com/Bigkoo/Android-PickerView/wiki/Methods-and-parameters)


## Thanks

- [WheelView](https://github.com/venshine/WheelView)
- [androidWheelView](https://github.com/weidongjian/androidWheelView/)

# License

```
Copyright 2014 Bigkoo
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```