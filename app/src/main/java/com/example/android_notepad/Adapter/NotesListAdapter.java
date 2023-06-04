package com.example.android_notepad.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_notepad.Models.Notes;
import com.example.android_notepad.NotesClickListener;
import com.example.android_notepad.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder> {

    private Context context;
    private List<Notes> list;
    private NotesClickListener listener;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private Uri photoUri;
    private ImageView imageView_photo;

    public NotesListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Створення макету для кожного елемента списку
        View itemView = LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        // Заповнення даними елемента списку
        Notes note = list.get(position);

        holder.textView_title.setText(note.getTitle());
        holder.textView_title.setSelected(true);

        holder.textView_notes.setText(note.getNotes());

        holder.textView_date.setText(note.getDate());
        holder.textView_date.setSelected(true);

        // Перевірка, чи позначка "пін" натиснута, і встановлення відповідної іконки
        if (note.isPinned()) {
            holder.imageView_pin.setImageResource(R.drawable.pin_icon);
        } else {
            holder.imageView_pin.setImageResource(0);
        }

        int colorCode = getRandomColor();
        holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));

        // Встановлення обробника подій при натисканні на елемент списку
        holder.notes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(note);
            }
        });

        // Встановлення обробника подій при довгому натисканні на елемент списку
        holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(note, holder.notes_container);
                return true;
            }
        });

        // Додавання обробника подій для кнопки додавання фотографії
        holder.imageView_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
    }

    private int getRandomColor() {
        // Створення списку рандомних кольорів
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);

        int random_color = (int) (Math.random() * colorCode.size());
        return colorCode.get(random_color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Notes> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    private void showPhotoDialog() {
        // Відображення діалогового вікна для вибору джерела фотографії
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Photo")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhotoFromCamera();
                                break;
                            case 1:
                                choosePhotoFromGallery();
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void takePhotoFromCamera() {
        // Перевірка дозволу на використання камери (для Android 6.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        // Запуск камери для зйомки фотографії
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void choosePhotoFromGallery() {
        // Вибір фотографії з галереї
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() {
        // Створення файлу для збереження фотографії
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Обробка результату з фотокамери або галереї
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageView_photo.setImageURI(photoUri);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                imageView_photo.setImageURI(selectedImageUri);
            }
        }
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {

        CardView notes_container;
        TextView textView_title, textView_notes, textView_date;
        ImageView imageView_pin, imageView_photo;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            // Знаходження елементів макету по їх ідентифікаторах
            notes_container = itemView.findViewById(R.id.notes_container);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_notes = itemView.findViewById(R.id.textView_notes);
            textView_date = itemView.findViewById(R.id.textView_date);
            imageView_pin = itemView.findViewById(R.id.imageView_pin);
            imageView_photo = itemView.findViewById(R.id.imageView_photo);
        }
    }
}
