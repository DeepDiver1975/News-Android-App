package de.luhmer.owncloudnewsreader.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import de.luhmer.owncloudnewsreader.R;
import de.luhmer.owncloudnewsreader.databinding.PodcastRowBinding;
import de.luhmer.owncloudnewsreader.events.podcast.AudioPodcastClicked;
import de.luhmer.owncloudnewsreader.events.podcast.StartDownloadPodcast;
import de.luhmer.owncloudnewsreader.helper.NewsFileUtils;
import de.luhmer.owncloudnewsreader.model.PodcastItem;

public class PodcastArrayAdapter extends ArrayAdapter<PodcastItem> {

    private LayoutInflater inflater;
    private EventBus eventBus;

    public PodcastArrayAdapter(Context context, PodcastItem[] values) {
        super(context, R.layout.podcast_row, values);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        eventBus = EventBus.getDefault();
        //eventBus.register(this);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            PodcastRowBinding binding = PodcastRowBinding.inflate(inflater, parent, false);
            view = binding.getRoot();
            holder = new ViewHolder(binding);
            view.setTag(holder);
        }

        final PodcastItem podcastItem = getItem(position);

        holder.binding.tvTitle.setText(podcastItem.title);
        holder.binding.tvBody.setText(podcastItem.mimeType);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPodcast(position);
            }
        });


        holder.binding.flDownloadPodcastWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.flDownloadPodcastWrapper.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Starting download.. Please wait", Toast.LENGTH_SHORT).show();

                eventBus.post(new StartDownloadPodcast() {{ podcast = podcastItem; }});
            }
        });

        holder.binding.flPlayPodcastWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPodcast(position);
            }
        });

        holder.binding.flDeletePodcastWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NewsFileUtils.deletePodcastFile(getContext(), podcastItem.link)) {
                    podcastItem.offlineCached = false;
                    podcastItem.downloadProgress = PodcastItem.DOWNLOAD_NOT_STARTED;
                    notifyDataSetChanged();
                }
            }
        });


        holder.binding.pbDownloadPodcast.setProgress(podcastItem.downloadProgress);
        if(podcastItem.downloadProgress >= 0) {
            holder.binding.tvDownloadPodcastProgress.setVisibility(View.VISIBLE);
            holder.binding.pbDownloadPodcast.setVisibility(View.VISIBLE);
            holder.binding.tvDownloadPodcastProgress.setText(podcastItem.downloadProgress + "%");
        }
        else {
            holder.binding.tvDownloadPodcastProgress.setVisibility(View.GONE);
            holder.binding.pbDownloadPodcast.setVisibility(View.GONE);
        }


        if(podcastItem.downloadProgress.equals(PodcastItem.DOWNLOAD_NOT_STARTED)) {
            holder.binding.flDownloadPodcastWrapper.setVisibility(View.VISIBLE);
        } else {
            holder.binding.flDownloadPodcastWrapper.setVisibility(View.GONE);
        }

        holder.binding.flDeletePodcastWrapper.setVisibility((podcastItem.downloadProgress.equals(PodcastItem.DOWNLOAD_COMPLETED)) ? View.VISIBLE : View.GONE );

        /*
        File podcastFile = new File(PodcastDownloadService.getUrlToPodcastFile(getContext(), podcastItem.link, true));
        File podcastFileCache = new File(PodcastDownloadService.getUrlToPodcastFile(getContext(), podcastItem.link, true) + ".download");
        if(podcastFile.exists()) {
            holder.flDownloadPodcast.setVisibility(View.GONE);
        }
        else if(podcastFileCache.exists()) {
            holder.flDownloadPodcast.setVisibility(View.GONE);
        }
        else
            holder.flDownloadPodcast.setVisibility(View.VISIBLE);
        */

        return view;
    }


    private void playPodcast(int position) {
        AudioPodcastClicked audioPodcastClicked = new AudioPodcastClicked();
        audioPodcastClicked.position = position;
        eventBus.post(audioPodcastClicked);
    }



    static class ViewHolder {
        @NonNull final PodcastRowBinding binding;

        public ViewHolder(@NonNull PodcastRowBinding binding) {
            this.binding = binding;
        }
    }
}
