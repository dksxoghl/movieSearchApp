package com.example.startproject2;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText editText1, editText2, editText3, editText4;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    ImageView imageView;
    PaintBoard paintBoard;
    Button erase;
    View view;
    File file;
    File signatureFile;
    ExifInterface exif = null;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        file = createFile();
        if (file.exists()) {
            System.out.println("사진파일 존재~~resume");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate -= 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate -= 90;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate -= 90;
            }

            if (rotate != 0) {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Matrix matrix = new Matrix();
                matrix.postRotate(-rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            imageView.setImageBitmap(bitmap);
//            Glide.with(this).asBitmap().load(file.getAbsolutePath()).error(R.drawable.knu_logo).into(imageView);
        }
        signatureFile = new File(getActivity().getExternalFilesDir(null),
                "signature.png");
        if (signatureFile.exists()) {
            System.out.println("사인파일존재resume");
            System.out.println(signatureFile.getAbsoluteFile() + "사인파일경로~~~~~~~~~~~");
            Bitmap bitmap = BitmapFactory.decodeFile(signatureFile.getAbsolutePath());
            paintBoard.changeBitmap(bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my, container, false);
        System.out.println("온크레이트 프래그먼트뷰");
        editText1 = rootView.findViewById(R.id.editText);
        editText2 = rootView.findViewById(R.id.editText2);
        editText3 = rootView.findViewById(R.id.editText3);
        editText4 = rootView.findViewById(R.id.editText4);
        radioButton1 = rootView.findViewById(R.id.radioButton);
        radioButton2 = rootView.findViewById(R.id.radioButton2);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        imageView = rootView.findViewById(R.id.imageView4);
//        paintBoard=new PaintBoard(getContext());
//View view=new Inflater().inflate(R.layout.fragment_my);
        paintBoard = rootView.findViewById(R.id.paintboard);
        erase = rootView.findViewById(R.id.erase);
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintBoard.mBitmap.eraseColor(Color.WHITE);
                paintBoard.invalidate();
                System.out.println("지우기~~");
            }
        });
        sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editText1.setText(sharedPreferences.getString("name", ""));
        editText2.setText(sharedPreferences.getString("birth", ""));
        editText3.setText(sharedPreferences.getString("email", ""));
        editText4.setText(sharedPreferences.getString("pass", ""));
        radioButton1.setChecked(sharedPreferences.getBoolean("r1", false));
        radioButton2.setChecked(sharedPreferences.getBoolean("r2", false));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    editor.putBoolean("r1", true);
                    editor.putBoolean("r2", false);
                    editor.commit();
                } else if (checkedId == R.id.radioButton2) {
                    editor.putBoolean("r2", true);
                    editor.putBoolean("r1", false);
                    editor.commit();
                }
            }
        });
        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText2.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                    }
                };
                GregorianCalendar cal = new GregorianCalendar();
                System.out.println(Calendar.YEAR + "연도 -------" + cal.get(Calendar.YEAR));
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                System.out.println(year + " " + month + " " + day);
                DatePickerDialog dialog = new DatePickerDialog(rootView.getContext(), dateSetListener, year, month, day);
                dialog.show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        signatureFile = new File(getActivity().getExternalFilesDir(null),
                "signature.png");
//        File signatureFile = new File("signature.png");
        Bitmap bitmap = paintBoard.mBitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        try {
            FileOutputStream fos = new FileOutputStream(signatureFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            System.out.println(signatureFile.getAbsoluteFile() + "  사인파일 저장?~~~~~~~~~~~~~~``");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
//
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_NORMAL:
//                return bitmap;
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                matrix.setScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.setRotate(180);
//                break;
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                matrix.setRotate(180);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_TRANSPOSE:
//                matrix.setRotate(90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.setRotate(90);
//                break;
//            case ExifInterface.ORIENTATION_TRANSVERSE:
//                matrix.setRotate(-90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.setRotate(-90);
//                break;
//            default:
//                return bitmap;
//        }
//        try {
//            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            bitmap.recycle();
//            return bmRotated;
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//            bitmap=rotateBitmap(bitmap,orientation);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate -= 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate -= 90;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate -= 90;
            }

            if (rotate != 0) {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Matrix matrix = new Matrix();
                matrix.postRotate(-rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            imageView.setImageBitmap(bitmap);
            System.out.println(file.getAbsolutePath() + "사진 파일~~~~~~~~~~~~");
//            Glide.with(this).asBitmap().load(file.getAbsolutePath()).error(R.drawable.knu_logo).into(imageView);
        }

    }

    private File createFile() {
        String filename = "capture.jpg";
        File storageDir = getActivity().getExternalFilesDir(null);
        File outFile = new File(storageDir, filename);
        return outFile;
    }

    private void takePicture() {
        if (file == null) {
            System.out.println("사진파일널일떄~~");
            file = createFile();
        }
        Uri fileUri = FileProvider.getUriForFile(getContext(), "com.example.startproject2.FileProvider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);


        editor.putString("name", editText1.getText().toString());
        editor.putString("birth", editText2.getText().toString());
        editor.putString("email", editText3.getText().toString());
        editor.putString("pass", editText4.getText().toString());

        //editor.putBoolean();

        editor.commit();


    }
}
