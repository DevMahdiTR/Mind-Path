package mindpath.core.service.playlist;


import com.fasterxml.jackson.databind.ObjectMapper;
import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.playlist.PlayList;
import mindpath.core.domain.playlist.PlayListDTO;
import mindpath.core.domain.playlist.UploadItemRequest;
import mindpath.core.domain.playlist.item.Item;
import mindpath.core.domain.playlist.item.ItemDTO;
import mindpath.core.domain.playlist.item.correction.Correction;
import mindpath.core.domain.playlist.item.exercice.Exercice;
import mindpath.core.domain.playlist.item.fiche.Fiche;
import mindpath.core.domain.playlist.item.qcm.Qcm;
import mindpath.core.domain.playlist.item.video.Video;
import mindpath.core.domain.subject.Subject;
import mindpath.core.exceptions.custom.ResourceNotFoundException;
import mindpath.core.mapper.playlist.PlayListDTOMapper;
import mindpath.core.mapper.playlist.item.ItemDTOMapper;
import mindpath.core.repository.playlist.PlayListRepository;
import mindpath.core.repository.subject.SubjectRepository;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import mindpath.core.service.playlist.item.ItemService;
import mindpath.core.service.playlist.item.ItemServiceFactory;
import mindpath.core.service.user.UserEntityService;
import mindpath.core.utility.annotation.IsAllowedToEditSubject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PlayListServiceImpl implements  PlayListService {

    private final PlayListRepository playListRepository;
    private final SubjectRepository subjectRepository;
    private final ItemServiceFactory itemServiceFactory;
    private final AzureStorageService azureStorageService;
    private final PlayListDTOMapper playListDTOMapper;
    private final UserEntityService userEntityService;
    private final ItemDTOMapper itemDTOMapper;


    public PlayListServiceImpl(PlayListRepository playListRepository, SubjectRepository subjectRepository, ItemServiceFactory itemServiceFactory, AzureStorageService azureStorageService, PlayListDTOMapper playListDTOMapper, UserEntityService userEntityService, ItemDTOMapper itemDTOMapper) {
        this.playListRepository = playListRepository;
        this.subjectRepository = subjectRepository;
        this.itemServiceFactory = itemServiceFactory;
        this.azureStorageService = azureStorageService;
        this.playListDTOMapper = playListDTOMapper;
        this.userEntityService = userEntityService;
        this.itemDTOMapper = itemDTOMapper;
    }



    @Override
    @IsAllowedToEditSubject
    public String addPlayListToSubject(
            @NotNull UserDetails userDetails,
            @NotNull Long subjectId,
            @NotNull PlayListDTO playListDTO
    ) {
        Subject subject = fetchSubjectById(subjectId);
        PlayList playList = PlayList.builder()
                .title(playListDTO.title())
                .description(playListDTO.description())
                .build();
        playList.setSubject(subject);
        playListRepository.save(playList);
        return "Liste de lecture ajoutée au sujet avec succès";
    }

    @Override
    @IsAllowedToEditSubject
    public String editPlayList(
            @NotNull UserDetails userDetails,
            @NotNull Long playListId,
            @NotNull Long subjectId,
            @NotNull PlayListDTO playListDTO
    ) {
        PlayList savedPlayList = getPlayListById(playListId);
        validatePlayList(savedPlayList, subjectId);
        savedPlayList.setTitle(playListDTO.title());
        savedPlayList.setDescription(playListDTO.description());
        playListRepository.save(savedPlayList);

        return "Liste de lecture modifiée avec succès";
    }

    @Override
    @IsAllowedToEditSubject
    public ItemDTO uploadItemToPlayList(
            @NotNull UserDetails userDetails,
            @NotNull Long playListId,
            @NotNull Long subjectId,
            @NotNull String uploadItemRequestJson,
            @NotNull MultipartFile file
            ) throws IOException {
        PlayList playList = getPlayListById(playListId);
        validatePlayList(playList, subjectId);

        UploadItemRequest uploadItemRequest = new ObjectMapper().readValue(uploadItemRequestJson, UploadItemRequest.class);

        if (uploadItemRequest.getCategory().equalsIgnoreCase("video") && !isValidVideoFile(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier vidéo non valide");
        }

        Item item = createItem(uploadItemRequest.getCategory(), uploadItemRequest.getTitle(), "n'est pas encore défini");
        ItemService<Item> itemItemService = (ItemService<Item>) itemServiceFactory.getItemService(item);
        Item newItem = itemItemService.addItemToPlayList(item, playList);

        azureStorageService.uploadLargeFile(file,file.getSize(),uploadItemRequest.getCategory().toLowerCase(),newItem);

        return itemDTOMapper.apply(newItem);
    }

    @Override
    @IsAllowedToEditSubject
    public void updatedItemUrl(@NotNull UserDetails userDetails,@NotNull Long subjectId,@NotNull Long itemId, @NotNull String url , @NotNull String category) {
        Item item = itemServiceFactory.getItemServiceById(itemId,category);
        item.setUrl(url);
        item.setCompleted(true);
        ItemService<Item> itemItemService = (ItemService<Item>) itemServiceFactory.getItemService(item);
        itemItemService.updateItem(item);
    }

    @Override
    public PlayListDTO fetchPlayListById(@NotNull Long playListId) {
        PlayList savePlayList = getPlayListById(playListId);
        return playListDTOMapper.apply(savePlayList);
    }

    @Override
    public PlayList getPlayListById(@NotNull Long playListId) {
        return playListRepository.fetchPlayListById(playListId).orElseThrow(
                () -> new ResourceNotFoundException("Liste de lecture avec l'ID : " + playListId + " non trouvée")
        );
    }

    @Override
    public void deleteItemFromPlayList(@NotNull UserDetails userDetails, @NotNull Long playListId, @NotNull Long itemId) {
        final UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        PlayList playList = getPlayListById(playListId);

        validateUserToDeletePlayList(user, playList);

        List<Item> items = new ArrayList<>();
        items.addAll(playList.getQcms());
        items.addAll(playList.getVideos());
        items.addAll(playList.getFiches());
        items.addAll(playList.getExercices());
        items.addAll(playList.getCorrections());


        if(items.stream().noneMatch(item -> item.getId().equals(itemId))) {
            throw new IllegalArgumentException("L'élément avec l'ID " + itemId + " n'appartient pas à la liste de lecture avec l'ID " + playListId);
        }
        Item item = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().get();
        ItemService<Item> itemItemService = (ItemService<Item>) itemServiceFactory.getItemService(item);
        if(!item.getUrl().equals("n'est pas encore défini")) {
            azureStorageService.deleteFile(item.getUrl());
        }
        itemItemService.deleteItemFromPlayList(item);
    }

    @Override
    public void deletePlayList(@NotNull UserDetails userDetails, @NotNull Long playListId) {
        final UserEntity user = userEntityService.fetchUserByEmail(userDetails.getUsername());
        PlayList playList = getPlayListById(playListId);

        validateUserToDeletePlayList(user, playList);

        deletePlayListAsync(playList);

        playListRepository.deletePlayListById(playListId);
    }

    @Override
    @Async("uploadTaskExecutor")
    public void deletePlayListAsync(@NotNull PlayList playList){

        for(Qcm qcm : playList.getQcms()){
            azureStorageService.deleteFile(qcm.getUrl());
        }

        for(Video video : playList.getVideos()){
            azureStorageService.deleteFile(video.getUrl());
        }

        for(Fiche fiche : playList.getFiches()){
            azureStorageService.deleteFile(fiche.getUrl());
        }

        for(Exercice exercice : playList.getExercices()){
            azureStorageService.deleteFile(exercice.getUrl());
        }

        for(Correction correction : playList.getCorrections()){
            azureStorageService.deleteFile(correction.getUrl());
        }


    }

    @Override
    public void deletePlayList(@NotNull PlayList playList){

        for(Qcm qcm : playList.getQcms()){
            azureStorageService.deleteFile(qcm.getUrl());
        }

        for(Video video : playList.getVideos()){
            azureStorageService.deleteFile(video.getUrl());
        }

        for(Fiche fiche : playList.getFiches()){
            azureStorageService.deleteFile(fiche.getUrl());
        }

        for(Exercice exercice : playList.getExercices()){
            azureStorageService.deleteFile(exercice.getUrl());
        }

        for(Correction correction : playList.getCorrections()){
            azureStorageService.deleteFile(correction.getUrl());
        }


    }

    private void validateUserToDeletePlayList(@NotNull UserEntity user, @NotNull PlayList playList){
        if(!user.getId().equals(playList.getSubject().getGroup().getSuperTeacher().getId())&&
                !user.getId().equals(playList.getSubject().getTeacher().getId())){
            throw new IllegalArgumentException("L'utilisateur n'est pas autorisé à supprimer cette liste de lecture");
        }
    }

    private @NotNull Item createItem(@NotNull String category, String title, String url) {
        Item item = switch (category.toLowerCase()) {
            case "video" -> new Video();
            case "qcm" -> new Qcm();
            case "fiche" -> new Fiche();
            case "exercice" -> new Exercice();
            case "correction" -> new Correction();
            default -> throw new IllegalArgumentException("Catégorie invalide : " + category);
        };
        item.setTitle(title);
        item.setUrl(url);
        item.setCompleted(false);
        return item;
    }

    private void validatePlayList(@NotNull final PlayList playList, @NotNull final Long subjectId) {
        if (!playList.getSubject().getId().equals(subjectId)) {
            throw new IllegalArgumentException("La liste de lecture avec l'ID " + playList.getId() + " n'appartient pas au sujet avec l'ID " + subjectId);
        }
    }

    private Subject fetchSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Sujet non trouvé"));
    }


    private boolean isValidVideoFile(String contentType) {
        List<String> allowedVideoTypes = Arrays.asList(
                "video/mp4",
                "video/mpeg",
                "video/x-msvideo",
                "video/x-ms-wmv",
                "video/quicktime",
                "video/x-matroska"
        );
        return allowedVideoTypes.contains(contentType);
    }

    private boolean isValidPdfFile(String contentType) {
        return "application/pdf".equals(contentType);
    }

}
